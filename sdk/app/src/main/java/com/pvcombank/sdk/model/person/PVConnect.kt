package com.pvcombank.sdk.model.person

import com.google.gson.annotations.SerializedName

data class PVConnect(
    @SerializedName("access")
	var access: Access,
    @SerializedName("createdTimestamp")
	var createdTimestamp: Long,
    @SerializedName("disableableCredentialTypes")
	var disableableCredentialTypes: List<Any>,
    @SerializedName("email")
	var email: String,
    @SerializedName("emailVerified")
	var emailVerified: Boolean,
    @SerializedName("enabled")
	var enabled: Boolean,
    @SerializedName("firstName")
	var firstName: String,
    @SerializedName("id")
	var id: String,
    @SerializedName("lastName")
	var lastName: String,
    @SerializedName("notBefore")
	var notBefore: Int,
    @SerializedName("requiredActions")
	var requiredActions: List<Any>,
    @SerializedName("totp")
	var totp: Boolean,
    @SerializedName("username")
	var username: String
){
	fun detected(): Boolean  {
		return username.isNotEmpty()
	}
	
	fun active(){
	
	}
}