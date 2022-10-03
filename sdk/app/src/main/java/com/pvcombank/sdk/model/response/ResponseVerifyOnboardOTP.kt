package com.pvcombank.sdk.model.response

import com.pvcombank.sdk.model.person.PVConnect
import com.google.gson.annotations.SerializedName

data class ResponseVerifyOnboardOTP(
	@SerializedName("pvconnect")
	var pvconnect: PVConnect,
	@SerializedName("ekyc")
	var ekyc: ResponseOCR,
	@SerializedName("accessToken")
	var token: String? = null
)
