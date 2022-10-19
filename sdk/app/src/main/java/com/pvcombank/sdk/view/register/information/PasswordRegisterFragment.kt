package com.pvcombank.sdk.view.register.information

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.pvcombank.sdk.base.PVFragment
import com.pvcombank.sdk.databinding.FragmentPasswordRegisterBinding
import com.pvcombank.sdk.model.Constants
import com.pvcombank.sdk.model.MasterModel
import com.pvcombank.sdk.model.ResponseData
import com.pvcombank.sdk.model.request.RequestFinish
import com.pvcombank.sdk.repository.OnBoardingRepository
import com.pvcombank.sdk.view.popup.AlertPopup
import com.pvcombank.sdk.view.register.SuccessFragment
import com.pvcombank.sdk.view.register.home.HomeFragment

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
				startUpdatePassword()
			}
		}
	}
	
	private fun FragmentPasswordRegisterBinding.startUpdatePassword() {
		repository.updatePassword(confirmPassword.getText()) {
			(it["success"] as? ResponseData<*>)?.let {
				//Cập nhập password thành công thì cập nhập thông tin cif
				startOpenCIF()
			}
			(it["fail"] as? String)?.let {
				val isEndAuth = it.contains("403") || it.contains("401")
				var message = it
				when {
					isEndAuth -> {
						message = "Phiên làm việc hết hạn."
					}
				}
				AlertPopup.show(
					fragmentManager = childFragmentManager,
					message = message,
					primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
						override fun onClickListener(v: View) {
							if (isEndAuth) {
								requireActivity().supportFragmentManager.popBackStack(
									null,
									FragmentManager.POP_BACK_STACK_INCLUSIVE
								)
								openFragment(
									HomeFragment::class.java,
									Bundle(),
									true
								)
							}
						}
					},
					primaryTitle = "OK"
				)
			}
		}
	}
	
	private fun startOpenCIF() {
		requireArguments().getParcelable<RequestFinish>("request_data_finish")
			?.let {
				repository.finish(it) {
					(it["success"] as? ResponseData<*>)?.let { response ->
						hideLoading()
						if (response.code == "1") {
							SuccessFragment(true).show(childFragmentManager, "SUCCESS")
						} else {
							SuccessFragment(false).show(childFragmentManager, "FAIL")
						}
					}
					(it["fail"] as? String)?.let { errorStr ->
						hideLoading()
						val isEndAuth = errorStr.contains("403") || errorStr.contains("401")
						var message = errorStr
						when {
							isEndAuth -> {
								message = "Phiên làm việc hết hạn."
							}
						}
						AlertPopup.show(
							fragmentManager = childFragmentManager,
							message = message,
							primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
								override fun onClickListener(v: View) {
									if (isEndAuth) {
										requireActivity().supportFragmentManager.popBackStack(
											null,
											FragmentManager.POP_BACK_STACK_INCLUSIVE
										)
										openFragment(
											HomeFragment::class.java,
											Bundle(),
											true
										)
									}
								}
							},
							primaryTitle = "OK"
						)
					}
				}
			} ?: kotlin.run {
			hideLoading()
			AlertPopup.show(
				fragmentManager = childFragmentManager,
				message = "Có lỗi vui lòng thử lại",
				primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
					override fun onClickListener(v: View) {
					}
				},
				primaryTitle = "OK"
			)
		}
	}
	
	override fun onBack(): Boolean = false
}