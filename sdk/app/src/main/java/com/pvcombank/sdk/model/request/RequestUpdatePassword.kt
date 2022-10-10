package com.pvcombank.sdk.model.request

import com.google.gson.annotations.SerializedName

data class RequestUpdatePassword(
	@SerializedName("password") var password: String? = null
)