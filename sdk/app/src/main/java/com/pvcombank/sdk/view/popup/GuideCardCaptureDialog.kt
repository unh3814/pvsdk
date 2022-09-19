package com.pvcombank.sdk.view.popup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.pvcombank.sdk.databinding.FragmentGuideCardCaptureBinding

class GuideCardCaptureDialog : DialogFragment() {
	private lateinit var viewBinding: FragmentGuideCardCaptureBinding
	private var listener: GuideCardDialogListener? = null
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		viewBinding = FragmentGuideCardCaptureBinding.inflate(inflater, container, false)
		return viewBinding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewBinding.apply {
			btnConfirm.setOnClickListener {
				(parentFragment as? GuideCardDialogListener)?.onConfirmClicked()
				dismissAllowingStateLoss()
			}
		}
	}
	
	override fun onStart() {
		super.onStart()
		dialog?.let {
			it.window?.apply {
				setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
			}
		}
	}
	
	fun show(fragmentManager: FragmentManager, arg: Bundle) {
		INSTANCE?.arguments = arg
		INSTANCE?.show(
			fragmentManager,
			this::class.java.simpleName
		)
	}
	
	interface GuideCardDialogListener {
		fun onConfirmClicked()
	}
	
	companion object {
		private var INSTANCE: GuideCardCaptureDialog? = null
		fun instance(): GuideCardCaptureDialog? {
			INSTANCE ?: GuideCardCaptureDialog().apply {
				INSTANCE = this
			}
			return INSTANCE
		}
	}
}