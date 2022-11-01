package com.pvcombank.sdk.payment.model.response

import com.pvcombank.sdk.payment.model.person.EKYC
import com.pvcombank.sdk.payment.model.person.PVConnect
import com.google.gson.annotations.SerializedName

data class ResponseVerifyOnboardOTP(
	@SerializedName("pvconnect")
	var pvconnect: PVConnect,
	@SerializedName("ekyc")
	var ekyc: EKYC
)
