package com.pvcombank.sdk.model.person


import com.google.gson.annotations.SerializedName

data class PVConnect(
    @SerializedName("active")
    var active: Boolean,
    @SerializedName("email")
    var email: String,
    @SerializedName("firstName")
    var firstName: String,
    @SerializedName("id")
    var id: String,
    @SerializedName("lastName")
    var lastName: String,
    @SerializedName("prevActive")
    var prevActive: Boolean,
    @SerializedName("username")
    var username: String
)