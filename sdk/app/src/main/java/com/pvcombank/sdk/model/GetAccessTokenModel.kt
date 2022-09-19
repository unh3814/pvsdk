package com.pvcombank.sdk.model

import com.google.gson.annotations.SerializedName

data class GetAccessTokenModel(
    @SerializedName("access_token")
    var accessToken: String,
    @SerializedName("expires_in")
    var expiresIn: Int,
    @SerializedName("id_token")
    var idToken: String,
    @SerializedName("not-before-policy")
    var notBeforePolicy: Int,
    @SerializedName("refresh_expires_in")
    var refreshExpiresIn: Int,
    @SerializedName("refresh_token")
    var refreshToken: String,
    @SerializedName("scope")
    var scope: String,
    @SerializedName("session_state")
    var sessionState: String,
    @SerializedName("token_type")
    var tokenType: String
)