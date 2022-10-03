package com.pvcombank.sdk.model

import com.google.gson.annotations.SerializedName

data class BranchModel(
    @SerializedName("ADDRESS")
    var aDDRESS: String,
    @SerializedName("BRANCHCODE")
    var bRANCHCODE: String,
    @SerializedName("BRANCHNAME")
    var bRANCHNAME: String,
    @SerializedName("BRANCHTYPE")
    var bRANCHTYPE: Any,
    @SerializedName("DISTRICTCODE")
    var dISTRICTCODE: String,
    @SerializedName("DISTRICTNAME")
    var dISTRICTNAME: String,
    @SerializedName("DISTRICTNAME_EN")
    var dISTRICTNAMEEN: String,
    @SerializedName("ID")
    var iD: Int,
    @SerializedName("POSTCODE")
    var pOSTCODE: String,
    @SerializedName("PROVINCECODE")
    var pROVINCECODE: String,
    @SerializedName("PROVINCENAME")
    var pROVINCENAME: String,
    @SerializedName("PROVINCENAME_VN")
    var pROVINCENAMEVN: String
)