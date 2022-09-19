package com.pvcombank.sdk.model

import com.google.gson.annotations.SerializedName

data class TransactionHistoryModel(
    @SerializedName("transAmount")
    var transAmount: Int,
    @SerializedName("transDescription")
    var transDescription: String,
    @SerializedName("transTime")
    var transTime: String,
    @SerializedName("transType")
    var transType: String
)