package com.pvcombank.sdk.model.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class RequestUnlockCard(
	@SerializedName("cardToken") var cardToken: String? = null
) : Parcelable