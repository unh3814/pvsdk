package com.pvcombank.sdk.view.customs.TextViewPVCB

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import com.pvcombank.sdk.R
import com.pvcombank.sdk.databinding.TextviewPvcombankBinding

class TextViewPVCB(
	context: Context,
	attributeSet: AttributeSet
) : LinearLayoutCompat(context, attributeSet) {
	private val viewBinding = TextviewPvcombankBinding.inflate(
		LayoutInflater.from(context),
		this,
		true
	)
	private var text = ""
	private var textTitle = ""
	private var isHeader = false
	private var isShowExpend = false
	private var isExpended = false
	private var viewExpend: View? = null
	init {
		context.theme.obtainStyledAttributes(
			attributeSet,
			R.styleable.TextViewPVCB,
			0,
			0
		).apply {
			try {
				text = getString(R.styleable.TextViewPVCB_contentText) ?: ""
				textTitle = getString(R.styleable.TextViewPVCB_titleText) ?: ""
				isHeader = getBoolean(R.styleable.TextViewPVCB_isHeader, false)
				isShowExpend = getBoolean(R.styleable.TextViewPVCB_showExpend, false)
				expendChange{
				
				}
				if(isHeader){
					viewBinding.content.visibility = View.GONE
					viewBinding.title.isAllCaps = true
					viewBinding.title.setTextColor(ContextCompat.getColor(context, R.color.color_blue))
					viewBinding.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
				} else {
					viewBinding.title.isAllCaps = false
					viewBinding.content.visibility = View.VISIBLE
				}
				if(isShowExpend){
					viewBinding.expend.visibility = View.VISIBLE
				} else {
					viewBinding.expend.visibility = View.GONE
				}
				setText()
				setTitle()
			} finally {
				recycle()
			}
		}
	}
	
	fun setText(value: String? = null) {
		text = value ?: text
		viewBinding.content.text = text
	}
	
	fun setTitle(value: String? = null) {
		textTitle = value ?: textTitle
		viewBinding.title.text = textTitle
	}
	
	fun setViewExpend(view: View){
		viewExpend = view
	}
	
	fun expendChange(callBack: (Boolean) -> Unit){
		if (isShowExpend){
			viewBinding.root.setOnClickListener {
				isExpended = !isExpended
				if(isExpended){
					viewBinding.expend.setImageResource(R.drawable.ic_round_keyboard_arrow_down_24)
				} else {
					viewBinding.expend.setImageResource(R.drawable.ic_round_keyboard_arrow_up_24)
				}
				callBack.invoke(isExpended)
			}
		}
	}
}