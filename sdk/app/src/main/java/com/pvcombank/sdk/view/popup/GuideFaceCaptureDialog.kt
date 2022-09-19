package com.pvcombank.sdk.view.popup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.pvcombank.sdk.databinding.FragmentGuideFaceCaptureBinding

class GuideFaceCaptureDialog : DialogFragment() {
	private lateinit var viewBinding: FragmentGuideFaceCaptureBinding
	private var listener: GuideFaceDialogListener? = null
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		viewBinding = FragmentGuideFaceCaptureBinding.inflate(inflater, container, false)
		return viewBinding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewBinding.apply {
			btnConfirm.setOnClickListener {
				(parentFragment as? GuideFaceDialogListener)?.onConfirmClicked()
				dismissAllowingStateLoss()
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
	
	interface GuideFaceDialogListener {
		fun onConfirmClicked()
	}
	
	companion object {
		private var INSTANCE: GuideFaceCaptureDialog? = null
		fun instance(): GuideFaceCaptureDialog? {
			INSTANCE ?: GuideFaceCaptureDialog().apply {
				INSTANCE = this
			}
			return INSTANCE
		}
	}
}