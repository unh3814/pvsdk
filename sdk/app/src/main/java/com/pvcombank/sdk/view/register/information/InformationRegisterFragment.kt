package com.pvcombank.sdk.view.register.information

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pvcombank.sdk.base.PVFragment
import com.pvcombank.sdk.databinding.FragmentRegisterBinding

class InformationRegisterFragment : PVFragment<FragmentRegisterBinding>() {
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		viewBinding = FragmentRegisterBinding.inflate(inflater, container, false)
		return viewBinding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewBinding.apply {
			topBar.setTitle("Đăng ký dịch vụ")
			btnConfirm.setOnClickListener {
				openFragment(
					PasswordRegisterFragment::class.java,
					Bundle(),
					true
				)
			}
		}
	}
	
	override fun onBack(): Boolean = false
}