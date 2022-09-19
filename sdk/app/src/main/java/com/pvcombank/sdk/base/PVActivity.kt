package com.pvcombank.sdk.base

import android.content.Context
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import com.pvcombank.sdk.base.model.AlertInline
import com.pvcombank.sdk.base.model.Loading
import com.pvcombank.sdk.base.model.TopBar

abstract class PVActivity<VB : ViewBinding> : FragmentActivity() {
	lateinit var viewBinding: VB
	lateinit var loading: Loading
	lateinit var alertInline: AlertInline
	lateinit var topBar: TopBar
	
	val handler = android.os.Handler(Looper.getMainLooper())
	
	override fun onStop() {
		super.onStop()
		handler.removeCallbacksAndMessages(null)
	}
	
	override fun onBackPressed() {
		if (!onBack()) supportFragmentManager.popBackStack()
	}
	
	abstract fun onBack(): Boolean
	
	fun goBack() = onBackPressed()
	
	fun hideKeyboard() {
		currentFocus?.let {
			(getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.apply {
				hideSoftInputFromWindow(it.windowToken, 0)
			}
		}
	}
	
	fun showSoftKeyboard(view: View) {
		if (view.requestFocus()) {
			val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
			imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
		}
	}
}