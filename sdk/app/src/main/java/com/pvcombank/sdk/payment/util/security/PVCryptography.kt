package com.pvcombank.sdk.payment.util.security

interface PVCryptography {
	fun encrypt(str: String): String
	fun decrypt(str: String? = null): String
}