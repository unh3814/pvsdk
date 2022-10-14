package com.pvcombank.sdk.view.register.after_create

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.pvcombank.sdk.base.PVFragment
import com.pvcombank.sdk.databinding.FragmentCreateAccoutBinding
import com.pvcombank.sdk.model.Constants
import com.pvcombank.sdk.model.MasterModel
import com.pvcombank.sdk.model.response.ResponseOCR
import com.pvcombank.sdk.model.response.ResponsePurchase
import com.pvcombank.sdk.network.ApiResponse
import com.pvcombank.sdk.repository.AuthRepository
import com.pvcombank.sdk.repository.OnBoardingRepository
import com.pvcombank.sdk.view.otp.confirm_otp.AuthOTPFragment
import com.pvcombank.sdk.view.popup.AlertPopup

class AfterCreateFragment : PVFragment<FragmentCreateAccoutBinding>() {
	private var repository: AuthRepository? = null
	private var onBoardingRepository: OnBoardingRepository? = null
	private val cache get() = MasterModel.getInstance().cache
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		viewBinding = FragmentCreateAccoutBinding.inflate(inflater, container, false)
		return viewBinding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewBinding.apply {
			topBar.show()
			topBar.setTitle("Điền thông tin cơ bản")
			repository = AuthRepository()
			onBoardingRepository = OnBoardingRepository()
			root.setOnClickListener {
				hideKeyboard()
			}
			MasterModel.getInstance().isCreateAccount = true
			MasterModel.getInstance().cleanOCR()
			root.setOnClickListener {
				hideKeyboard()
			}
			phoneNumber.addTextChangeListener {
				it?.let {
					val str = it.toString()
					if (str.matches(Constants.regexPhone) && str.startsWith("0")) {
						cache["phone_number"] = it.toString()
						phoneNumber.setError("")
						btnCreateAccount.isEnabled = true
					} else if (str.isNullOrEmpty()) {
						phoneNumber.setError("")
						btnCreateAccount.isEnabled = false
					} else {
						phoneNumber.setError("Số điện thoại không hợp lệ, vui lòng thử lại")
						btnCreateAccount.isEnabled = false
					}
				} ?: kotlin.run {
					phoneNumber.setError("Vui lòng nhập số điện thoại")
					cache.remove("phone_number")
					btnCreateAccount.isEnabled = false
				}
			}
			emailAddress.addTextChangeListener {
				it?.let {
					val str = it.toString()
					if (str.matches(Constants.regexEmail)) {
						cache["email"] = it.toString()
						emailAddress.setError("")
						if (phoneNumber.getText()
								.matches(Constants.regexPhone) && phoneNumber.getText()
								.startsWith("0")
						) {
							btnCreateAccount.isEnabled = true
						}
					} else if (str.isNullOrEmpty()) {
						emailAddress.setError("")
						if (phoneNumber.getText()
								.matches(Constants.regexPhone) && phoneNumber.getText()
								.startsWith("0")
						) {
							btnCreateAccount.isEnabled = true
						}
					} else {
						btnCreateAccount.isEnabled = false
						emailAddress.setError("Email không đúng định dạng, vui lòng thử lại")
					}
				} ?: kotlin.run {
					cache.remove("email")
					btnCreateAccount.isEnabled = true
					emailAddress.setError("")
				}
			}
			btnCreateAccount.setOnClickListener {
				val cachePhoneNumber = (cache["phone_number"] as? String) ?: ""
				if (cachePhoneNumber.isEmpty() || cachePhoneNumber.length != 10) {
					phoneNumber.setError("Số điện thoại không hợp lệ, vui lòng thử lại")
				} else {
					showLoading()
					repository?.sendOTP(
						phoneNumber = (cache["phone_number"] as? String) ?: "",
						email = (cache["email"] as? String) ?: ""
					) {
						hideLoading()
						if (it !is String) {
							openFragment(
								AuthOTPFragment::class.java,
								requireArguments(),
								false
							)
						} else {
							AlertPopup.show(
								fragmentManager = childFragmentManager,
								title = "Thông báo",
								message = "${it}",
								primaryTitle = "OK",
								primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
									override fun onClickListener(v: View) {
									
									}
								}
							)
						}
					}
				}
			}
		}
	}
	
	override fun onStart() {
		super.onStart()
		viewBinding.apply {
			phoneNumber.setText((cache["phone_number"] as? String) ?: "")
			emailAddress.setText((cache["email"] as? String) ?: "")
		}
		hideLoading()
	}
	
	override fun onBack(): Boolean = false
}