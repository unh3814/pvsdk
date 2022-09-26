package com.pvcombank.sdk.view.register.information

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pvcombank.sdk.base.PVFragment
import com.pvcombank.sdk.databinding.FragmentPasswordRegisterBinding
import com.pvcombank.sdk.view.register.SuccessFragment

class PasswordRegisterFragment : PVFragment<FragmentPasswordRegisterBinding>() {
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
			topBar.setTitle("Đăng ký dịch vụ ngân hàng số")
			btnRegister.setOnClickListener {
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