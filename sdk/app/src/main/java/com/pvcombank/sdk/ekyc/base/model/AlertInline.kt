package com.pvcombank.sdk.ekyc.base.model

import android.graphics.drawable.Drawable

interface AlertInline {
	fun show(icon: Drawable?, message: String)
	fun hide()
}