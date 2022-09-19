package com.pvcombank.sdk.model

import com.google.gson.annotations.SerializedName

data class ErrorBody(
    @SerializedName("code")
    var code: String,
    @SerializedName("message")
    var message: String
)