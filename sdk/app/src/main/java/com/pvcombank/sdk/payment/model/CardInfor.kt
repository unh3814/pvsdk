package com.pvcombank.sdk.payment.model

import com.google.gson.annotations.SerializedName

class CardInfor {
    private val address = ""
    private val addressconf: String? = null
    
    private val birthday = ""
    private val birthdayconf: String? = null
    
    private val ethnicity = ""
    private val expiry = ""
    private val id = ""
    private val id_type = "" //0 mặt trước, 1 mặt sau
    
    
    private val issue_by = ""
    private val issue_by_conf: String? = null
    
    private val issue_date = ""
    private val issue_date_conf: String? = null
    
    private var name = ""
    private val nameconf: String? = null
    
    private val religion = ""
    private val sex = ""
    private val national = ""
    private val country = ""
    
    @SerializedName("class")
    private val cardClass = ""
    private val province = ""
    private val district = ""
    private val precinct = ""
    
    private val hometown: String? = null
    private val hometownconf: String? = null
    
    private val copyright: String? = null
    private val idconf: String? = null
    
    @SerializedName("server_name")
    private val serverName: String? = null
    
    @SerializedName("server_ver")
    private val serverVer: String? = null
    
    private val characteristics: String? = null
    private val characteristics_conf: String? = null
    
    private val document: String? = null
    private val id_check: String? = null
    private val id_logic: String? = null
    private val id_logic_message: String? = null
    private val result_code: Long = 0
    private val street: String? = null
    private val street_name: String? = null
    
    override fun toString(): String {
        return """
             CardInfo{address='$address'
             , addressconf='$addressconf'
             , birthday='$birthday'
             , birthdayconf='$birthdayconf'
             , ethnicity='$ethnicity'
             , expiry='$expiry'
             , id='$id'
             , id_type='$id_type'
             , issue_by='$issue_by'
             , issue_by_conf='$issue_by_conf'
             , issue_date='$issue_date'
             , issue_date_conf='$issue_date_conf'
             , name='$name'
             , nameconf='$nameconf'
             , religion='$religion'
             , sex='$sex'
             , national='$national'
             , country='$country'
             , cardClass='$cardClass'
             , province='$province'
             , district='$district'
             , precinct='$precinct'
             , hometown='$hometown'
             , hometownconf='$hometownconf'
             , copyright='$copyright'
             , idconf='$idconf'
             , serverName='$serverName'
             , serverVer='$serverVer'
             , characteristics='$characteristics'
             , characteristics_conf='$characteristics_conf'
             , document='$document'
             , id_check='$id_check'
             , id_logic='$id_logic'
             , id_logic_message='$id_logic_message'
             , result_code=$result_code, street='$street'
             , street_name='$street_name'
             }
             """.trimIndent()
    }
}