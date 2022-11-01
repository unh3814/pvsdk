package com.pvcombank.sdk.payment.model.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class RequestLockCard(
	@SerializedName("cardToken") var cardToken: String? = null
): Parcelable