package com.pvcombank.sdk.view.register.after_create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pvcombank.sdk.base.PVFragment
import com.pvcombank.sdk.databinding.FragmentCreateAccoutBinding
import com.pvcombank.sdk.model.MasterModel
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
		topBar.setTitle("Điền thông tin cơ bản")
		repository = AuthRepository()
		MasterModel.getInstance().isCreateAccount = true
		viewBinding.apply {
			this.root.setOnClickListener {
				hideKeyboard()
			}
			txtToLogin.setOnClickListener {
				goBack()
			}
			phoneNumber.addTextChangeListener {
			
			}
			emailAddress.addTextChangeListener {
			
			}
//			editPhoneNumber.addTextChangedListener {
//				phoneString = (it ?: "").toString()
//			}
//			editEmail.addTextChangedListener {
//				emailString = (it ?: "").toString()
//			}
			btnCreateAccount.setOnClickListener {
				showLoading()
				MasterModel.getInstance().cacheCreateAccountMail = emailString ?: ""
				MasterModel.getInstance().cacheCreateAccountPhone = phoneString ?: ""
				repository?.sendOTP(
					phoneNumber = phoneString!!,
					email = emailString!!
				) {
					hideLoading()
					if (it !is String) {
						openFragment(
							AuthOTPFragment::class.java,
							Bundle(),
							true
						)
					}
				}
			}
		}
	}
	
	override fun onBack(): Boolean = false
}