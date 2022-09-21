package com.pvcombank.sdk.view.customs.TextViewPVCB

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.widget.LinearLayoutCompat
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
	
	init {
	
	}
}