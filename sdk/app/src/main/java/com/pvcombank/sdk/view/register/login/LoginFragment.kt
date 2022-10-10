package com.pvcombank.sdk.view.register.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import com.pvcombank.sdk.base.PVFragment
import com.pvcombank.sdk.databinding.FragmentLoginBinding
import com.pvcombank.sdk.model.Constants
import com.pvcombank.sdk.view.register.after_create.AfterCreateFragment

class LoginFragment : PVFragment<FragmentLoginBinding>(){
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		viewBinding = FragmentLoginBinding.inflate(inflater, container, false)
		return viewBinding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewBinding.apply {
			topBar.hide()
			root.setOnClickListener {
				hideKeyboard()
			}
			username.addTextChangeListener {
				validateData()
			}
			passoword.addTextChangeListener {
				validateData()
			}
			btnToLogin.setOnClickListener {
			
			}
			btnRegister.setOnClickListener {
				openFragment(
					AfterCreateFragment::class.java,
					arguments ?: Bundle(),
					true
				)
			}
		}
	}
	
	fun FragmentLoginBinding.validateData(){
		btnToLogin.isEnabled = (passoword.getText().matches(Constants.regexPassword) && username.getText().isNotEmpty())
	}
	
	override fun onBack(): Boolean = false
}