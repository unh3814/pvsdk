package com.pvcombank.sdk.ekyc.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class ResponsePurchase(
	@SerializedName("uuid") var uuid: String? = null,
	@SerializedName("phoneNumber") var phoneNumber: String? = null
): Parcelable