package com.pvcombank.sdk.view.register

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.pvcombank.sdk.R
import com.pvcombank.sdk.databinding.FragmentRegisterSuccessBinding
import com.pvcombank.sdk.model.MasterModel
import com.pvcombank.sdk.util.Utils.handleUrlClicks

class SuccessFragment(private val isSuccess: Boolean = false) : DialogFragment() {
	lateinit var viewBinding: FragmentRegisterSuccessBinding
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
			requireArguments().apply {
				if (isSuccess) {
					tvSuccess.text = getString(R.string.last_step_success_title)
					tvMessageSuccess.text = getString(R.string.last_step_success_message)
					imgIcon.setImageResource(R.drawable.success)
					tvMessageSuccess.handleUrlClicks{
						val target = Intent().also {
							it.action = Intent.ACTION_CALL
							it.data = Uri.parse("tel: 190055592")
						}
						startActivity(Intent.createChooser(target, "Select"))
					}
				} else {
					tvSuccess.text = getString(R.string.last_step_fail_title)
					tvMessageSuccess.text = getString(R.string.last_step_fail_message)
					imgIcon.setImageResource(R.drawable.frame_last_fail)
				}
				
			}
			btnConfirm.setOnClickListener {
				MasterModel.getInstance().cleanOCR()
				requireActivity().finish()
			}
		}
	}
	
	override fun onStart() {
		super.onStart()
		dialog?.let {
			it.window?.apply {
				setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
			}
			it.setCanceledOnTouchOutside(false)
		}
	}
}