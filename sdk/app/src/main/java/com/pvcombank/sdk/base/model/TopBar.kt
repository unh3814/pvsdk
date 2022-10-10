package com.pvcombank.sdk.base.model

import android.graphics.drawable.Drawable
import android.view.View
import com.pvcombank.sdk.R
import com.pvcombank.sdk.databinding.TopBarBinding

interface TopBarInterface {
	fun show()
	fun hide()
}

interface TopBarListener {
	interface BackListener {
		fun onBackClick()
	}
	
	interface MoreListener {
		fun onMoreClick()
	}
}

class TopBar : TopBarInterface {
	private lateinit var viewBinding: TopBarBinding
	
	fun setColor(color: Int){
		viewBinding.llRootView.setBackgroundResource(color)
	}
	
	fun setContentView(viewBinding: TopBarBinding){
		this.viewBinding = viewBinding
		viewBinding.tvTitle.visibility = View.VISIBLE
	}
	
	fun setTitle(value: String) {
		viewBinding.tvTitle.text = value
	}
	
	fun addBackListener(listener: TopBarListener.BackListener) {
		viewBinding.imgBack.setOnClickListener {
			listener.onBackClick()
		}
	}
	
	fun addButtonMore(
		title: String? = null,
		drawable: Drawable? = null,
		listener: TopBarListener.MoreListener
	) {
		viewBinding.apply {
			tvCancel.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
			if (title.isNullOrEmpty()){
				tvCancel.visibility = View.INVISIBLE
				tvCancel.isClickable = false
			} else {
				tvCancel.text = title
				tvCancel.setOnClickListener {
					listener.onMoreClick()
				}
			}
		}
	}
	
	override fun show() {
		viewBinding.llRootView.visibility = View.VISIBLE
	}
	
	fun showButtonBack(){
		viewBinding.imgBack.visibility = View.VISIBLE
		viewBinding.imgBack.isClickable = true
		viewBinding.imgBack.isFocusable = true
	}
	
	override fun hide() {
		viewBinding.llRootView.visibility = View.GONE
	}
	
	fun hideButtonBack(){
		viewBinding.imgBack.visibility = View.INVISIBLE
		viewBinding.imgBack.isClickable = false
		viewBinding.imgBack.isFocusable = false
	}
	
	companion object {
		private var INSTANCE: TopBar? = null
		fun build(): TopBar {
			return INSTANCE ?: synchronized(this) {
				INSTANCE ?: TopBar().also {
					INSTANCE = it
				}
			}
		}
	}
}