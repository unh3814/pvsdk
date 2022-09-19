package com.pvcombank.sdk.view.register.select

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pvcombank.sdk.base.PVFragment
import com.pvcombank.sdk.databinding.FragmentSelectMethodOcrBinding
import com.pvcombank.sdk.view.popup.GuideCardCaptureDialog
import com.pvcombank.sdk.view.register.scan.card.CardCaptureFragment

class SelectMethodFragment : PVFragment<FragmentSelectMethodOcrBinding>(), GuideCardCaptureDialog.GuideCardDialogListener {
	private var methodSelected: Int? = null
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		viewBinding = FragmentSelectMethodOcrBinding
			.inflate(
				inflater,
				container,
				false
			)
		return viewBinding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewBinding.apply {
			handleSelectMethod()
			btnNext.setOnClickListener {
				showAlert()
			}
		}
	}
	
	private fun FragmentSelectMethodOcrBinding.handleSelectMethod() {
		methodSelected?.let {
			rbSelectMethod.check(it)
		} ?: kotlin.run {
			rbSelectMethod.check(rbCccd.id)
			methodSelected = rbCccd.id
		}
		rbSelectMethod.setOnCheckedChangeListener { _, checkedId ->
			methodSelected = checkedId
		}
	}
	
	override fun onConfirmClicked() {
		openFragment(
			CardCaptureFragment::class.java,
			Bundle(),
			true
		)
	}
	
	private fun showAlert() {
		GuideCardCaptureDialog
			.instance()
			?.show(
				childFragmentManager,
				Bundle()
			)
	}
	
	override fun onBack(): Boolean = false
}