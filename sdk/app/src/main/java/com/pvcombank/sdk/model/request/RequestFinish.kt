package com.pvcombank.sdk.model.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RequestFinish(
	@SerializedName("currentAddr") var currentAddress: String? = "", // Địa chỉ hiện tại
	@SerializedName("reside") var reside: Boolean = true, // Cư trú
	@SerializedName("job") var job: String? = null, // Nghề nghiệp
	@SerializedName("introducer") var introducer: String? = "", // Người giới thiệu
	@SerializedName("nationality") var nationality: String? = "Việt Nam", // Quốc tịch
	@SerializedName("agentCode") var agentCode: String? = "NHS", // Mã đại lý
	@SerializedName("gender") var gender: String? = "FEMALE", // MALE|FEMALTE
	@SerializedName("permanentAddr") var permanentAddr: String = "", // Địa chỉ thường trú
	@SerializedName("nativePlace") var nativePlace: String = "", // Nguyên quán
	@SerializedName("accountNumber") var accountNumber: String? = "",
	@SerializedName("fatca") var fatca: Boolean = false, // Fat Ca
	@SerializedName("delegate") var delegate: Boolean = false, // Ủy nhiệm
	@SerializedName("productCode") var productCode: String? = "ONBOARD", // Gói offer sản phẩm
	@SerializedName("channel") var channel: String? = "MB", // Kênh (chưa dùng)
	@SerializedName("signature") var signature: String? = "", // Base64 ảnh chữ ký
	@SerializedName("cardDeliveryType") var cardDeliveryType: String? = "AGENT", // AGENT|ADDRESS (chi nhánh/khác)
	@SerializedName("cardDeliveryAddress") var cardDeliveryAddress: String? = "",// Địa chỉ nhận thẻ
	@SerializedName("branchCode") var branchCode: String? = "",// Địa chỉ nhận thẻ
	@SerializedName("expiredDate") var expiredDate: String? = ""// Địa chỉ nhận thẻ
): Parcelable