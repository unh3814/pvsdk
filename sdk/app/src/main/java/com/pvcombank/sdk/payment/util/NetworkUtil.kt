package com.pvcombank.sdk.payment.util

import com.pvcombank.sdk.payment.model.request.RequestModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.HttpException

object NetworkUtil {
    private val gson = Gson()
    fun Throwable.getErrorBody(): RequestModel<Any>? {
        return (this as? HttpException)?.response()?.let { resonse ->
            val typeResponse = object : TypeToken<RequestModel<Any>>() {}.type
            val result = RequestModel<Any>()
            result.code = resonse.code().toString()
            resonse.errorBody()?.let {
                try {
                    val model = gson.fromJson<RequestModel<Any>>(it.string(), typeResponse)
                    result.data = model.data
                    result.code = model.code
                    result.message = model.message
                    return result
                } catch (e: Exception) {
                    return result
                }
            }
            return result
        }
    }
}