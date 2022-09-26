package com.pvcombank.sdk.model.response


import com.google.gson.annotations.SerializedName

data class ResponseOCR(
	@SerializedName("cardImageId") var cardImageId: Any,
	@SerializedName("cardLabel") var cardLabel: Any,
	@SerializedName("cardType") var cardType: Any,
	@SerializedName("cifNumber") var cifNumber: Any,
	@SerializedName("dob") var dob: Any,
	@SerializedName("email") var email: Any,
	@SerializedName("error") var error: Any,
	@SerializedName("expDate") var expDate: Any,
	@SerializedName("gender") var gender: Any,
	@SerializedName("getCifNumber") var getCifNumber: Any,
	@SerializedName("idNumber") var idNumber: Any,
	@SerializedName("issueDate") var issueDate: Any,
	@SerializedName("issuePlace") var issuePlace: Any,
	@SerializedName("mobilePhone") var mobilePhone: Any,
	@SerializedName("name") var name: Any,
	@SerializedName("nationality") var nationality: Any,
	@SerializedName("nativePlace") var nativePlace: Any,
	@SerializedName("permanentAddress") var permanentAddress: Any,
	@SerializedName("phone") var phone: Any,
	@SerializedName("placeOfBirth") var placeOfBirth: Any,
	@SerializedName("signature") var signature: Any,
	@SerializedName("signatureUpdateTime") var signatureUpdateTime: Any,
	@SerializedName("status") var status: Any
)