package com.pvcombank.sdk.payment.model.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RequestVerifyOTP(
	@SerializedName("uuid") var uuid: String? = null,
	@SerializedName("otp") var otp: String? = null
) : Parcelable