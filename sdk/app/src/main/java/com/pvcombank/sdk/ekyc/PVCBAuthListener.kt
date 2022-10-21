package com.pvcombank.sdk.ekyc

interface

PVCBAuthListener {
	fun onSuccess(message: String)
	fun onError(message: String)
}