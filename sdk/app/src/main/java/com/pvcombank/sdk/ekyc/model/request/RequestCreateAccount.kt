package com.pvcombank.sdk.ekyc.model.request

data class RequestCreateAccount(
	var email: String? = "",
	var phoneNumber: String? = null
)
