package com.pvcombank.sdk.view.register.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pvcombank.sdk.base.PVFragment
import com.pvcombank.sdk.databinding.FragmentHomeBinding
import com.pvcombank.sdk.view.login.AuthWebLoginFragment
import com.pvcombank.sdk.view.register.after_create.AfterCreateFragment
import com.pvcombank.sdk.view.register.login.LoginFragment

class HomeFragment : PVFragment<FragmentHomeBinding>() {
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		viewBinding = FragmentHomeBinding.inflate(inflater, container, false)
		return viewBinding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewBinding.apply {
			topBar.hide()
			btnToLogin.setOnClickListener {
				openFragment(
					AuthWebLoginFragment::class.java,
					arguments ?: Bundle(),
					true
				)
			}
			
			btnToRegister.setOnClickListener {
				openFragment(
					AfterCreateFragment::class.java,
					arguments ?: Bundle(),
					true
				)
			}
		}
	}
	
	override fun onBack(): Boolean = false
}