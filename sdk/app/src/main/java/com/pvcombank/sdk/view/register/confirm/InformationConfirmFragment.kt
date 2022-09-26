package com.pvcombank.sdk.view.register.confirm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pvcombank.sdk.base.PVFragment
import com.pvcombank.sdk.databinding.FragmentCardCaptureResultBinding
import com.pvcombank.sdk.view.register.information.InformationRegisterFragment

class InformationConfirmFragment : PVFragment<FragmentCardCaptureResultBinding>() {
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		viewBinding = FragmentCardCaptureResultBinding.inflate(inflater, container, false)
		return viewBinding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewBinding.apply {
			topBar.setTitle("Xác nhận thông tin")
			btnConfirm.setOnClickListener {
				openFragment(
					InformationRegisterFragment::class.java,
					Bundle(),
					true
				)
			}
		}
	}
	
	override fun onBack(): Boolean = false
}