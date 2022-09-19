package com.pvcombank.sdk.model.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RequestPurchase(
	@SerializedName("amount") val amount: Long? = 50000L,
	@SerializedName("cardToken") val cardToken: String? = "4c975c887c484823a7399c4cd6a2d3e3",
	@SerializedName("description") val description: String? = "Thanh toan vien phi tai BV",
	@SerializedName("traceNumber") val traceNumber: String? = "005002"
) : Parcelable