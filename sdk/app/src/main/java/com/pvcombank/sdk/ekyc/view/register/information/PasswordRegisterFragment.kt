package com.pvcombank.sdk.ekyc.view.register.information

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.pvcombank.sdk.ekyc.R
import com.pvcombank.sdk.ekyc.base.PVFragment
import com.pvcombank.sdk.ekyc.databinding.FragmentPasswordRegisterBinding
import com.pvcombank.sdk.ekyc.model.Constants
import com.pvcombank.sdk.ekyc.model.MasterModel
import com.pvcombank.sdk.ekyc.model.request.RequestFinish
import com.pvcombank.sdk.ekyc.repository.OnBoardingRepository
import com.pvcombank.sdk.ekyc.view.popup.AlertPopup
import com.pvcombank.sdk.ekyc.view.register.SuccessFragment
import com.pvcombank.sdk.ekyc.view.register.after_create.AfterCreateFragment

class PasswordRegisterFragment : PVFragment<FragmentPasswordRegisterBinding>() {
	private val repository = OnBoardingRepository()
	private val cache get() = MasterModel.getInstance().cache
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		viewBinding = FragmentPasswordRegisterBinding.inflate(inflater, container, false)
		return viewBinding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewBinding.apply {
			topBar.show()
			topBar.setTitle("Đăng ký dịch vụ ngân hàng số")
			root.setOnClickListener {
				hideKeyboard()
			}
			val data = MasterModel.getInstance().getDataOCR()
			name.setText(data.name)
			identity.setText(data.mobilePhone ?: (cache["phone_number"] as? String) ?: "")
			password.addTextChangeListener {
				it?.let {
					val str = it.toString()
					if (str.matches(Constants.regexPassword)) {
						if (confirmPassword.getText() == str) {
							btnRegister.isEnabled = true
						} else {
							btnRegister.isEnabled = false
							confirmPassword.setError("Mật khẩu không chính xác")
						}
						password.setNote("Mật khẩu tối thiểu 6 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt")
					} else {
						btnRegister.isEnabled = false
						password.setNote("")
						password.setError("Mật khẩu tối thiểu 6 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt")
					}
				}
			}
			confirmPassword.addTextChangeListener {
				if (it.toString() != password.getText()) {
					confirmPassword.setError("Mật khẩu không chính xác")
					btnRegister.isEnabled = false
				} else {
					confirmPassword.setError("")
					btnRegister.isEnabled = password.getText().matches(Constants.regexPassword)
				}
			}
			btnRegister.setOnClickListener {
				showLoading()
				repository.updatePassword(confirmPassword.getText())
			}
			repository.observerUpdatePassword
				.observe(
					viewLifecycleOwner,
					Observer {
						it?.let {
							startOpenCIF()
						}
					}
				)
			repository.observerFinish.observe(
				viewLifecycleOwner,
				Observer {
					it?.let {
						//Cập nhập xong
					}
				}
			)
			repository.error.observe(
				viewLifecycleOwner,
				Observer {
					it?.let {
						hideLoading()
						if (it.first == 0) {
							SuccessFragment(false).show(childFragmentManager, "FAIL")
						} else {
							AlertPopup.show(
								fragmentManager = childFragmentManager,
								message = it.second,
								primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
									override fun onClickListener(v: View) {
										if (it.first in (401..499)) {
											openFragment(
												AfterCreateFragment::class.java,
												Bundle()
											)
										}
									}
								},
								primaryTitle = getString(R.string.txt_close)
							)
						}
					}
				}
			)
		}
	}
	
	private fun startOpenCIF() {
		hideLoading()
		SuccessFragment(true).show(childFragmentManager, "SUCCESS")
		requireArguments().getParcelable<RequestFinish>("request_data_finish")
			?.let {
				repository.finish(it)
			}
			?: kotlin.run {
				AlertPopup.show(
					fragmentManager = childFragmentManager,
					message = "Có lỗi vui lòng thử lại",
					primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
						override fun onClickListener(v: View) {
						}
					},
					primaryTitle = getString(R.string.txt_close)
				)
			}
	}
	
	override fun onBack(): Boolean = false
}