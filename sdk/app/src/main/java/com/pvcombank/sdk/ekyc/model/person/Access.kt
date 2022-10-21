package com.pvcombank.sdk.ekyc.model.person

import com.google.gson.annotations.SerializedName

data class Access(
	@SerializedName("impersonate")
	var impersonate: Boolean,
	@SerializedName("manage")
	var manage: Boolean,
	@SerializedName("manageGroupMembership")
	var manageGroupMembership: Boolean,
	@SerializedName("mapRoles")
	var mapRoles: Boolean,
	@SerializedName("view")
	var view: Boolean
)
