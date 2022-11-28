package com.pvcombank.sdk.ekyc.model.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.pvcombank.sdk.ekyc.model.Constants
import com.pvcombank.sdk.ekyc.model.MasterModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class AuthToken(
	@SerializedName("client_id")
	var clientId: String? = Constants.CLIENT_ID,
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
) : Parcelable