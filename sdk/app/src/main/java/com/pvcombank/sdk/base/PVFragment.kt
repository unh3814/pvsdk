package com.pvcombank.sdk.base

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.pvcombank.sdk.util.Utils
import com.pvcombank.sdk.util.Utils.openFragment
import com.pvcombank.sdk.view.popup.AlertPopup

abstract class PVFragment<VB : ViewBinding> : Fragment() {
	lateinit var viewBinding: VB
	abstract fun onBack(): Boolean
	val handler = Handler(Looper.getMainLooper())
	val topBar get() = (requireActivity() as PVActivity<*>).topBar
	
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
				title = "Thông báo",
				message = "Quá thời gian thao tác, vui lòng thực hiện lại.",
				primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
					override fun onClickListener(v: View) {
						requireActivity().recreate()
					}
				},
				primaryTitle = "OK"
			)
		}
	}
	
	fun openFragment(
		clazz: Class<out Fragment>,
		arg: Bundle,
		addBackStack: Boolean
	) = requireActivity().openFragment(clazz, arg, addBackStack)
	
	fun showLoading() {
		hideKeyboard()
		when (val activity = requireActivity()) {
			is PVActivity<*> -> activity.loading.show()
		}
	}
	
	fun hideLoading() {
		when (val activity = requireActivity()) {
			is PVActivity<*> -> activity.loading.hide()
		}
	}
	
	fun showInlineMessage(icon: Drawable? = null, message: String) {
		when (val activity = requireActivity()) {
			is PVActivity<*> -> activity.alertInline.show(icon, message)
		}
	}
	
	fun hideInlineMessage() {
		when (val activity = requireActivity()) {
			is PVActivity<*> -> activity.alertInline.hide()
		}
	}
	
	fun showToastMessage(message: String){
		Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
	}
	
	fun hideKeyboard() = (requireActivity() as? PVActivity<*>)?.hideKeyboard()
	
	fun showKeyboard(view: View) = (requireActivity() as? PVActivity<*>)?.showSoftKeyboard(view)
	
	fun goBack() = (requireActivity() as? PVActivity<*>)?.goBack()
}