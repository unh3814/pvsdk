package com.pvcombank.sdk.view.popup

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.pvcombank.sdk.R
import com.pvcombank.sdk.databinding.FragmentPopupBinding
import com.pvcombank.sdk.model.MasterModel

class AlertPopup : DialogFragment() {
	private var secondButtonListener: SecondButtonListener? = null
	private var primaryButtonListener: PrimaryButtonListener? = null
	
	private lateinit var viewBinding: FragmentPopupBinding
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		viewBinding = FragmentPopupBinding.inflate(inflater, container, false)
		return viewBinding.root
	}
	
	override fun onStart() {
		super.onStart()
		dialog?.let {
			it.window?.apply {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
					setBackgroundBlurRadius(8)
				} else{
					setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
				}
				setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
			}
		}
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewBinding.apply {
			root.setBackgroundResource(R.color.color_white)
			val titleSecond = arguments?.getString(TITLE_SECOND)
			val titlePrimary = arguments?.getString(TITLE_PRIMARY)
			val icon = arguments?.getInt(ICON)
			if (icon != null && icon != -1) {
				imgAlert.setImageResource(icon)
				imgAlert.visibility = View.VISIBLE
			} else {
				imgAlert.visibility = View.GONE
			}
			btnSecond.visibility = if (titleSecond.isNullOrEmpty()) {
				View.GONE
			} else {
				dialog?.setCanceledOnTouchOutside(false)
				btnSecond.setOnClickListener {
					if (secondButtonListener == null) {
						dismissAllowingStateLoss()
					} else {
						dismissAllowingStateLoss()
						secondButtonListener?.onClickListener(it)
					}
				}
				btnSecond.text = titleSecond
				View.VISIBLE
			}
			
			btnPrimary.visibility = if (titlePrimary.isNullOrEmpty()) {
				View.GONE
			} else {
				dialog?.setCanceledOnTouchOutside(false)
				btnPrimary.setOnClickListener {
					if (primaryButtonListener == null) {
						dismissAllowingStateLoss()
					} else {
						dismissAllowingStateLoss()
						primaryButtonListener?.onClickListener(it)
					}
				}
				btnPrimary.text = titlePrimary
				View.VISIBLE
			}
			if (AUTO_FINISH != null && AUTO_FINISH!! > 0) {
				val countTime = object : CountDownTimer(AUTO_FINISH!!, 1000) {
					override fun onTick(millisUntilFinished: Long) {
						try {
							tvMessage.text = getString(
								R.string.payment_success,
								MasterModel.getInstance().clientId,
								(millisUntilFinished / 1000).toString()
							)
							btnPrimary.text = "Quay về ${MasterModel.getInstance().clientId}"
						} catch (e: Exception){
						
						}
					}
					
					override fun onFinish() {
						try {
							requireActivity().finish()
						} catch (e: Exception){
						
						}
					}
				}
				countTime.start()
			} else {
				arguments?.getString(MESSAGE)?.let {
					tvMessage.text = it
				}
			}
		}
	}
	
	companion object {
		private const val ICON = "popup.icon"
		private const val TITLE = "popup.title"
		private const val MESSAGE = "popup.message"
		private const val TITLE_SECOND = "popup.title.second"
		private const val TITLE_PRIMARY = "popup.title.primary"
		private var AUTO_FINISH: Long? = null
		fun show(
			icon: Int? = null,
			fragmentManager: FragmentManager,
			message: String? = null,
			secondTitle: String? = null,
			secondButtonListener: SecondButtonListener? = null,
			primaryTitle: String? = null,
			primaryButtonListener: PrimaryButtonListener? = null,
			autoFinish: Long? = null
		) {
			AlertPopup().apply {
				this.secondButtonListener = secondButtonListener
				this.primaryButtonListener = primaryButtonListener
				arguments = Bundle().apply {
					putInt(ICON, icon ?: -1)
					putString(MESSAGE, message)
					putString(TITLE_SECOND, secondTitle)
					putString(TITLE_PRIMARY, primaryTitle)
					AUTO_FINISH = autoFinish
				}
			}.show(fragmentManager, AlertPopup::class.java.simpleName)
		}
	}
	
	interface SecondButtonListener {
		fun onClickListener(v: View)
	}
	
	interface PrimaryButtonListener {
		fun onClickListener(v: View)
	}
}