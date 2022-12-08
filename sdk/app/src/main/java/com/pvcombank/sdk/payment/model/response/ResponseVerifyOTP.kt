package com.pvcombank.sdk.payment.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResponseVerifyOTP(
    @SerializedName("refNumber")
    var refNumber: String? = null,
    @SerializedName("traceNumber")
    var traceNumber: String? = null,
    @SerializedName("dateTimePurchase")
    var dateTimePurchase: String? = null
) : Parcelable