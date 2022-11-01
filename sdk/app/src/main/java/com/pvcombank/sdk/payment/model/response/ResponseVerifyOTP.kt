package com.pvcombank.sdk.payment.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResponseVerifyOTP(
	@SerializedName("refNumber")
	var refNumber: String,
	@SerializedName("traceNumber")
	var traceNumber: String
) : Parcelable