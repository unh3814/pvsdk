package com.pvcombank.sdk.view.register.information

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pvcombank.sdk.base.PVFragment
import com.pvcombank.sdk.databinding.FragmentPasswordRegisterBinding
import com.pvcombank.sdk.model.Constants
import com.pvcombank.sdk.model.MasterModel
import com.pvcombank.sdk.repository.OnBoardingRepository
import com.pvcombank.sdk.view.register.SuccessFragment

class PasswordRegisterFragment : PVFragment<FragmentPasswordRegisterBinding>() {
	private val repository = OnBoardingRepository()
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
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
			identity.setText(data.mobilePhone ?: MasterModel.getInstance().cacheCreateAccountPhone)
			password.addTextChangeListener {
				it?.let {
					val str = it.toString()
					if (str.matches(Constants.regexPassword)){
						if (confirmPassword.getText() == str){
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
				if (it.toString() != password.getText()){
					confirmPassword.setError("Mật khẩu không chính xác")
					btnRegister.isEnabled = false
				} else {
					confirmPassword.setError("")
					btnRegister.isEnabled = password.getText().matches(Constants.regexPassword)
				}
			}
			btnRegister.setOnClickListener {
				repository.updatePassword(confirmPassword.getText())
				openFragment(
					SuccessFragment::class.java,
					Bundle(),
					false
				)
			}
		}
	}
	
	override fun onBack(): Boolean = false
}