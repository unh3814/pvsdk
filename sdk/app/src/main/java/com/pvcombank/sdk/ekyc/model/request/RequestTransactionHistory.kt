package com.pvcombank.sdk.ekyc.model.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RequestTransactionHistory(
	@SerializedName("cardToken") val cardToken: String? = null,
	@SerializedName("startDate") val startDate: String? = null,
	@SerializedName("endDate") val endDate: String? = null
) : Parcelable