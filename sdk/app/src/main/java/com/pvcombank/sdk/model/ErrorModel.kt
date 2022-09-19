package com.pvcombank.sdk.model

import com.google.gson.annotations.SerializedName

data class ErrorModel(
    @SerializedName("code")
    var code: String,
    @SerializedName("detail")
    var detail: String,
    @SerializedName("status")
    var status: String,
    @SerializedName("title")
    var title: String,
    @SerializedName("type")
    var type: String
)