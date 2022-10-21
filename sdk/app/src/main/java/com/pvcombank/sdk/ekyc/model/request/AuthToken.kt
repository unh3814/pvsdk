package com.pvcombank.sdk.ekyc.model.request

import android.os.Parcelable
import com.pvcombank.sdk.ekyc.model.Constants
import com.pvcombank.sdk.ekyc.model.MasterModel
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@Parcelize
data class AuthToken(
	@SerializedName("client_id")
	var clientId: String? = MasterModel.getInstance().clientId,
	var username: String? = null,
	var password: String? = null,
	@SerializedName("grant_type")
	var granType: String = Constants.GRANT_TYPE_CODE,
	@SerializedName("refresh_token")
	var refreshToken: String? = null,
	@SerializedName("client_secret")
	var clientSecret: String? = MasterModel.getInstance().clientSecret,
	@SerializedName("code")
	var code: String? = null,
	@SerializedName("redirect_uri")
	var redirectUri: String? = null
) : Parcelable {
	fun getRequest(): RequestBody {
		val gson = Gson()
		val mediaType = "application/x-www-form-urlencoded".toMediaTypeOrNull()
		val content = gson.toJson(this)
		return content.toRequestBody(mediaType)
	}
}