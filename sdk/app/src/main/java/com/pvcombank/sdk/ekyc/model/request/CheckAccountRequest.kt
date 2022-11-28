package com.pvcombank.sdk.ekyc.model.request

import com.google.gson.annotations.SerializedName

data class CheckAccountRequest(
	@SerializedName("idNumber") val idNumber: String? = "", // Số giấy tờ
	@SerializedName("idType") val idType: String? = "" ,  // Loại image giấy tờ
	@SerializedName("phone") val phone: String? = "" // Số điện thoại
)