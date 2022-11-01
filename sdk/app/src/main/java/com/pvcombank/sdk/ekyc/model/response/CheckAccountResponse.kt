package com.pvcombank.sdk.ekyc.model.response

import com.google.gson.annotations.SerializedName

data class CheckAccountResponse(
	@SerializedName("C") val C: String? = null,
	@SerializedName("D") val D: String? = null,
	@SerializedName("H") val H: String? = null,
	@SerializedName("E") val E: E? = null
)

data class E(
	@SerializedName("STATUS") val status: String? = null,
	@SerializedName("MESSAGE") val message: String? = null
)