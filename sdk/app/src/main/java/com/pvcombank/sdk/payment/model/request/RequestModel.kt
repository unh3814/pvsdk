package com.pvcombank.sdk.payment.model.request

import com.google.gson.Gson
import com.pvcombank.sdk.payment.util.security.SecurityHelper

data class RequestModel<T>(
    var data: T? = null,
    var message: String? = null,
    var code: String? = null,
    var timeStamp: String? = null
) {
    private val securityHelper = SecurityHelper.instance().cryptoBuild(type = SecurityHelper.AES)
    fun request(): T = data!!
    fun encrypt(): RequestModel<Any> {
        return RequestModel(
            data = securityHelper?.encrypt(Gson().toJson(data))
        )
    }
}
