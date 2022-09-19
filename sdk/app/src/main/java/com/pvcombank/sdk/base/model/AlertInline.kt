package com.pvcombank.sdk.base.model

import android.graphics.drawable.Drawable

interface AlertInline {
	fun show(icon: Drawable?, message: String)
	fun hide()
}