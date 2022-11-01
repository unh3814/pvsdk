package com.pvcombank.sdk.payment.model.request

data class RequestCreateAccount(
	var email: String? = "",
	var phoneNumber: String? = null
)
