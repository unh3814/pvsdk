package com.pvcombank.sdk

interface

PVCBAuthListener {
	fun onSuccess(message: String)
	fun onError(message: String)
}