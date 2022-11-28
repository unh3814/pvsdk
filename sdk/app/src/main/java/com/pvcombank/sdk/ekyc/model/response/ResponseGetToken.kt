package com.pvcombank.sdk.ekyc.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResponseGetToken(
	@SerializedName("access_token") val accessToken: String? = null,
	@SerializedName("expires_in") val expiresIn: Int? = null,
	@SerializedName("refresh_expires_in") val refreshExpiresIn: Int? = null,
	@SerializedName("refresh_token") val refreshToken: String? = null,
	@SerializedName("token_type") val tokenType: String? = null,
	@SerializedName("not-before-policy") val notBeforePolicy: Int? = null,
	@SerializedName("session_state") val sessionState: String? = null,
	@SerializedName("scope") val scope: String? = null
) : Parcelable