package com.pvcombank.sdk.view.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pvcombank.sdk.base.PVFragment
import com.pvcombank.sdk.databinding.FragmentRegisterSuccessBinding
import com.pvcombank.sdk.model.MasterModel

class SuccessFragment : PVFragment<FragmentRegisterSuccessBinding>() {
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		viewBinding = FragmentRegisterSuccessBinding.inflate(inflater, container, false)
		return viewBinding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewBinding.apply {
			btnConfirm.setOnClickListener {
				MasterModel.getInstance().cleanOCR()
				requireActivity().finish()
			}
		}
	}
	
	override fun onBack(): Boolean = false
}