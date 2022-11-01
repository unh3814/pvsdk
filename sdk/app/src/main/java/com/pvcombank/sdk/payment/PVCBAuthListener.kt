package com.pvcombank.sdk.payment

interface

PVCBAuthListener {
	fun onSuccess(message: String)
	fun onError(message: String)
}