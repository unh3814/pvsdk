package com.pvcombank.sdk.ekyc.model.request

import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@Parcelize
data class RequestTransactionHistory(
	@SerializedName("cardToken") val cardToken: String? = null,
	@SerializedName("startDate") val startDate: String? = null,
	@SerializedName("endDate") val endDate: String? = null
) : Parcelable {
	fun getRequest(): RequestBody {
		val gson = Gson()
		val mediaType = "application/json".toMediaTypeOrNull()
		val content = gson.toJson(this)
		return content.toRequestBody(mediaType)
	}
}