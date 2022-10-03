package com.pvcombank.sdk.view.register.after_create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pvcombank.sdk.base.PVFragment
import com.pvcombank.sdk.databinding.FragmentCreateAccoutBinding
import com.pvcombank.sdk.model.Constants
import com.pvcombank.sdk.model.MasterModel
import com.pvcombank.sdk.model.response.ResponseOCR
import com.pvcombank.sdk.model.response.ResponsePurchase
import com.pvcombank.sdk.repository.AuthRepository
import com.pvcombank.sdk.view.otp.confirm_otp.AuthOTPFragment

class AfterCreateFragment : PVFragment<FragmentCreateAccoutBinding>() {
	private var emailString: String? = null
	private var phoneString: String? = null
	private var repository: AuthRepository? = null
	
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
			MasterModel.getInstance().isCreateAccount = true
			MasterModel.getInstance().cleanOCR()
			root.setOnClickListener {
				hideKeyboard()
			}
			phoneNumber.setText("")
			emailAddress.setText("")
			phoneNumber.addTextChangeListener {
				it?.let {
					val str = it.toString()
					if (str.matches(Constants.regexPhone)) {
						phoneString = it.toString()
						phoneNumber.setError("")
					} else if (str.isNullOrEmpty()) {
						phoneNumber.setError("")
					} else {
						phoneNumber.setError("Số điện thoại không hợp lệ, vui lòng thử lại")
					}
				} ?: kotlin.run {
					phoneNumber.setError("Vui lòng nhập số điện thoại")
					phoneString = ""
				}
			}
			emailAddress.addTextChangeListener {
				it?.let {
					val str = it.toString()
					if (str.matches(Constants.regexEmail)) {
						emailString = it.toString()
						emailAddress.setError("")
					} else if (str.isNullOrEmpty()) {
						emailAddress.setError("")
					} else {
						emailAddress.setError("Email không đúng định dạng, vui lòng thử lại")
					}
				} ?: kotlin.run {
					emailString = ""
					emailAddress.setError("")
				}
			}
			btnCreateAccount.setOnClickListener {
				if (phoneString.isNullOrEmpty() || (phoneString?.length ?: 0) != 10) {
					phoneNumber.setError("Số điện thoại không hợp lệ, vui lòng thử lại")
				} else {
					MasterModel.getInstance().cacheCreateAccountPhone = phoneString ?: ""
				}
				if (MasterModel.getInstance().cacheCreateAccountPhone.isNotEmpty()) {
					showLoading()
					repository?.sendOTP(
						phoneNumber = phoneString ?: "",
						email = emailString ?: ""
					) {
						hideLoading()
						if (it !is String) {
							openFragment(
								AuthOTPFragment::class.java,
								Bundle(),
								false
							)
						}
					}
				}
			}
		}
	}
	
	override fun onStart() {
		super.onStart()
		hideLoading()
	}
	
	override fun onBack(): Boolean = false
}