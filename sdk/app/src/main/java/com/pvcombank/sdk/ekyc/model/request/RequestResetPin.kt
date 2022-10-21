package com.pvcombank.sdk.ekyc.model.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RequestResetPin(
	@SerializedName("cardToken") var cardToken: String? = null
) : Parcelable