package com.pvcombank.sdk.model.response


import com.google.gson.annotations.SerializedName

data class ResponseOCR(
	@SerializedName("cardImageId") var cardImageId: String? = null,
	@SerializedName("cardLabel") var cardLabel: String? = null,
	@SerializedName("cardType") var cardType: String? = null,
	@SerializedName("cifNumber") var cifNumber: String? = null,
	@SerializedName("dob") var dob: String? = null,
	@SerializedName("email") var email: String? = null,
	@SerializedName("error") var error: String? = null,
	@SerializedName("errorMessage") var errorMessage: String? = null,
	@SerializedName("expDate") var expDate: String? = null,
	@SerializedName("gender") var gender: Boolean? = false,
	@SerializedName("getCifNumber") var getCifNumber: String? = null,
	@SerializedName("idNumber") var idNumber: String? = null,
	@SerializedName("issueDate") var issueDate: String? = null,
	@SerializedName("issuePlace") var issuePlace: String? = null,
	@SerializedName("mobilePhone") var mobilePhone: String? = null,
	@SerializedName("name") var name: String? = null,
	@SerializedName("nationality") var nationality: String? = null,
	@SerializedName("nativePlace") var nativePlace: String? = null,
	@SerializedName("permanentAddress") var permanentAddress: String? = null,
	@SerializedName("phone") var phone: String? = null,
	@SerializedName("placeOfBirth") var placeOfBirth: String? = null,
	@SerializedName("signature") var signature: String? = null,
	@SerializedName("signatureUpdateTime") var signatureUpdateTime: String? = null,
	@SerializedName("status") var status: String? = null,
	@SerializedName("bankData") var bankData: List<Any>? = null,
	@SerializedName("step") var step: Int? = null
)