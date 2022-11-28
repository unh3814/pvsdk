package com.pvcombank.sdk.ekyc.model

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
    
    
    fun getAddress(): String? {
        return address
    }
    
    fun getBirthday(): String? {
        return birthday
    }
    
    fun getEthnicity(): String? {
        return ethnicity
    }
    
    fun getExpiry(): String? {
        return expiry
    }
    
    fun getID(): String? {
        return id
    }
    
    /**
     * 0 mặt trước
     * 1 mặt sau
     */
    fun getIDType(): String? {
        return id_type
    }
    
    fun getIssueBy(): String? {
        return issue_by
    }
    
    fun getIssueDate(): String? {
        return issue_date
    }
    
    fun getName(): String? {
        return name
    }
    
    fun setName(value: String) {
        name = value
    }
    
    fun getReligion(): String? {
        return religion
    }
    
    fun getResultCode(): Long {
        return result_code
    }
    
    fun getSex(): String? {
        return sex
    }
    
    fun getId(): String? {
        return id
    }
    
    fun getId_type(): String? {
        return id_type
    }
    
    fun getIssue_by(): String? {
        return issue_by
    }
    
    fun getIssue_date(): String? {
        return issue_date
    }
    
    fun getNational(): String? {
        return national
    }
    
    fun getCountry(): String? {
        return country
    }
    
    fun getCardClass(): String? {
        return cardClass
    }
    
    fun getProvince(): String? {
        return province
    }
    
    fun getDistrict(): String? {
        return district
    }
    
    fun getPrecinct(): String? {
        return precinct
    }
    
    fun getCopyright(): String? {
        return copyright
    }
    
    fun getIdconf(): String? {
        return idconf
    }
    
    fun getServerName(): String? {
        return serverName
    }
    
    fun getServerVer(): String? {
        return serverVer
    }
    
    fun getAddressconf(): String? {
        return addressconf
    }
    
    fun getBirthdayconf(): String? {
        return birthdayconf
    }
    
    fun getIssue_by_conf(): String? {
        return issue_by_conf
    }
    
    fun getIssue_date_conf(): String? {
        return issue_date_conf
    }
    
    fun getNameconf(): String? {
        return nameconf
    }
    
    fun getHometown(): String? {
        return hometown
    }
    
    fun getHometownconf(): String? {
        return hometownconf
    }
    
    fun getCharacteristics(): String? {
        return characteristics
    }
    
    fun getCharacteristics_conf(): String? {
        return characteristics_conf
    }
    
    fun getDocument(): String? {
        return document
    }
    
    fun getId_check(): String? {
        return id_check
    }
    
    fun getId_logic(): String? {
        return id_logic
    }
    
    fun getId_logic_message(): String? {
        return id_logic_message
    }
    
    fun getResult_code(): Long {
        return result_code
    }
    
    fun getStreet(): String? {
        return street
    }
    
    fun getStreet_name(): String? {
        return street_name
    }
    
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