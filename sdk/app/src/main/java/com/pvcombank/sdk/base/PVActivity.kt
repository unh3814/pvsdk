package com.pvcombank.sdk.base

import android.content.Context
import android.os.Bundle
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
	open var fragmentHostID: Int = 0
	
	val handler = android.os.Handler(Looper.getMainLooper())

	override fun onStop() {
		super.onStop()
		handler.removeCallbacksAndMessages(null)
	}

	override fun onBackPressed() {
		if (!onBack()) supportFragmentManager.popBackStack()
	}
	
	abstract fun onBack(): Boolean
	
	fun goBack(fragmentId: Int? = null) {
		fragmentId?.let {
			supportFragmentManager.popBackStack(fragmentId, 0)
		} ?: kotlin.run {
			onBackPressed()
		}
	}
	
	fun hideKeyboard() {
		currentFocus?.let {
			it.clearFocus()
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
	
	fun initLoading(view: View) {
		loading = object : Loading {
			override fun show() {
				view.visibility = View.VISIBLE
			}
			
			override fun hide() {
				view.visibility = View.GONE
			}
		}
	}
}