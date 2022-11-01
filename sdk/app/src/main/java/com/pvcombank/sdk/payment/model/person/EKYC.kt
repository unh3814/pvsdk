package com.pvcombank.sdk.payment.model.person

import com.google.gson.annotations.SerializedName

data class EKYC(
	@SerializedName("contactAddress")
	var contactAddress: Any?,
	@SerializedName("dateOfBirth")
	var dateOfBirth: Any?,
	@SerializedName("documentExpireDate")
	var documentExpireDate: Any?,
	@SerializedName("documentIssueDate")
	var documentIssueDate: Any?,
	@SerializedName("documentNumber")
	var documentNumber: Any?,
	@SerializedName("ekycStatus")
	var ekycStatus: Any?,
	@SerializedName("fullName")
	var fullName: Any?,
	@SerializedName("gender")
	var gender: Any?,
	@SerializedName("phoneNumber")
	var phoneNumber: String?,
	@SerializedName("placeOfOrigin")
	var placeOfOrigin: Any?
){
	fun isNeedEKYC(): Boolean {
		return ekycStatus == null
	}
}
