package com.pvcombank.sdk.payment.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class CardModel(
	@SerializedName("availableBalance") var availableBalance: String,
	@SerializedName("cardStatus") var cardStatus: String,
	@SerializedName("expDate") var expDate: String,
	@SerializedName("holderName") var holderName: String,
	@SerializedName("issDate") var issDate: String,
	@SerializedName("maxLimit") var maxLimit: Int,
	@SerializedName("minLimit") var minLimit: Int,
	@SerializedName("number") var numberCard: String,
	@SerializedName("cardType") var cardType: String? = null,
	@SerializedName("type") var type: String? = null,
	@SerializedName("source") var source: String? = null,
	@SerializedName("cardToken") var cardToken: String? = null
) : Parcelable{
	@IgnoredOnParcel
	var isSelected = false
}