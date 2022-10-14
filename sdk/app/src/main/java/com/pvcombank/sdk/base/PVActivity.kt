package com.pvcombank.sdk.base

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import com.pvcombank.sdk.R
import com.pvcombank.sdk.base.model.AlertInline
import com.pvcombank.sdk.base.model.Loading
import com.pvcombank.sdk.base.model.TopBar
import com.pvcombank.sdk.model.MasterModel
import com.pvcombank.sdk.util.Utils
import com.pvcombank.sdk.view.popup.AlertPopup

abstract class PVActivity<VB : ViewBinding> : FragmentActivity() {
	lateinit var viewBinding: VB
	lateinit var loading: Loading
	lateinit var alertInline: AlertInline
	lateinit var topBar: TopBar
	open var fragmentHostID: Int = 0
	
	val handler = android.os.Handler(Looper.getMainLooper())
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		MasterModel.getInstance().timeLogin = null
	}
	
	override fun onStop() {
		super.onStop()
		handler.removeCallbacksAndMessages(null)
	}
	
	override fun onResume() {
		super.onResume()
		if (Utils.checkOutOfTime()) {
			AlertPopup.show(
				fragmentManager = supportFragmentManager,
				title = "Thông báo",
				message = "Quá thời gian thao tác, vui lòng thực hiện lại.",
				primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
					override fun onClickListener(v: View) {
						recreate()
					}
				},
				primaryTitle = "OK"
			)
		}
	}
	
	override fun onBackPressed() {
		if (!onBack()) supportFragmentManager.popBackStack()
	}
	
	abstract fun onBack(): Boolean
	
	fun goBack() = onBackPressed()
	
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