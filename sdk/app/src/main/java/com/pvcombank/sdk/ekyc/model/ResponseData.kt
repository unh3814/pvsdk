package com.pvcombank.sdk.ekyc.model

import com.google.gson.annotations.SerializedName

data class ResponseData<T>(
	@SerializedName("code")
	var code: String,
	@SerializedName("message")
	var message: String,
	@SerializedName("data")
	var data: T? = null,
	@SerializedName("timestamp")
	var timeStamp: String? = null
)