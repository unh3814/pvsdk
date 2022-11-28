package com.pvcombank.sdk.ekyc.model.response

import com.pvcombank.sdk.ekyc.model.person.PVConnect
import com.google.gson.annotations.SerializedName

data class ResponseVerifyOnboardOTP(
	@SerializedName("pvconnect")
	var pvconnect: PVConnect,
	@SerializedName("ekyc")
	var ekyc: ResponseOCR,
	@SerializedName("accessToken")
	var token: String? = null
)
