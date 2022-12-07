package com.pvcombank.sdk.ekyc.base

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.appsflyer.AppsFlyerLib
import com.pvcombank.sdk.ekyc.R
import com.pvcombank.sdk.ekyc.model.Constants
import com.pvcombank.sdk.ekyc.util.Utils
import com.pvcombank.sdk.ekyc.util.Utils.getDeviceSpecificID
import com.pvcombank.sdk.ekyc.util.Utils.openFragment
import com.pvcombank.sdk.ekyc.util.Utils.timeToString
import com.pvcombank.sdk.ekyc.view.popup.AlertPopup
import java.util.*

abstract class PVFragment<VB : ViewBinding> : Fragment() {
	lateinit var viewBinding: VB
	abstract fun onBack(): Boolean
	val handler = Handler(Looper.getMainLooper())
	val topBar get() = (requireActivity() as com.pvcombank.sdk.ekyc.base.PVActivity<*>).topBar
	
	override fun onStart() {
		super.onStart()
		hideKeyboard()
	}
	override fun onStop() {
		super.onStop()
		handler.removeCallbacksAndMessages(null)
	}
	
	override fun onResume() {
		super.onResume()
		if (Utils.checkOutOfTime()) {
			AlertPopup.show(
				fragmentManager = childFragmentManager,
				message = "Quá thời gian thao tác, vui lòng thực hiện lại.",
				primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
					override fun onClickListener(v: View) {
						requireActivity().recreate()
					}
				},
				primaryTitle = getString(R.string.txt_close)
			)
		}
	}
	
	fun openFragment(
		clazz: Class<out Fragment>,
		arg: Bundle,
		addBackStack: Boolean? = true
	) = requireActivity().openFragment(clazz, arg, addBackStack!!)
	
	fun showLoading() {
		hideKeyboard()
		when (val activity = requireActivity()) {
			is com.pvcombank.sdk.ekyc.base.PVActivity<*> -> activity.loading.show()
		}
	}
	
	fun hideLoading() {
		when (val activity = requireActivity()) {
			is com.pvcombank.sdk.ekyc.base.PVActivity<*> -> activity.loading.hide()
		}
	}
	
	fun showInlineMessage(icon: Drawable? = null, message: String) {
		when (val activity = requireActivity()) {
			is com.pvcombank.sdk.ekyc.base.PVActivity<*> -> activity.alertInline.show(icon, message)
		}
	}
	
	fun hideInlineMessage() {
		when (val activity = requireActivity()) {
			is com.pvcombank.sdk.ekyc.base.PVActivity<*> -> activity.alertInline.hide()
		}
	}
	
	fun showToastMessage(message: String){
		Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
	}
	
	fun hideKeyboard() = (requireActivity() as? com.pvcombank.sdk.ekyc.base.PVActivity<*>)?.hideKeyboard()
	
	fun showKeyboard(view: View) = (requireActivity() as? com.pvcombank.sdk.ekyc.base.PVActivity<*>)?.showSoftKeyboard(view)
	
	fun goBack() = (requireActivity() as? com.pvcombank.sdk.ekyc.base.PVActivity<*>)?.goBack()
	fun goBack(fragmentId: Int) = (requireActivity() as? com.pvcombank.sdk.ekyc.base.PVActivity<*>)?.goBack(fragmentId)

	fun logEvent(className: String, event: String, data: MutableMap<String, Any>){
		data["af_time"] = Date().time.timeToString(Constants.MARCOM_DATE_TIME)
		data["af_device_id"] = getDeviceSpecificID()
		data["af_device_os"] = Build.VERSION.BASE_OS
		data["af_ekyc_step"] = className
		data["af_channel"] = Constants.APP_CODE ?: ""

		AppsFlyerLib.getInstance().logEvent(
			requireContext(),
			event,
			data
		)
	}
}