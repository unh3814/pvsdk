package com.pvcombank.sdk.util

import ResponseData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.HttpException

object NetworkUtil {
	private val gson = Gson()
	fun Throwable.getErrorBody(): ResponseData<Any>? {
		return (this as? HttpException)?.response()?.let { resonse ->
			val typeResponse = object : TypeToken<ResponseData<Any>>() {}.type
			val result = ResponseData<Any>()
			result.code = resonse.code().toString()
			resonse.errorBody()?.let {
				try {
					val model = gson.fromJson<ResponseData<Any>>(it.string(), typeResponse)
					result.data = model.data
					result.code = model.code ?: "0"
					result.message = model.message ?: ""
					return result
				} catch (e: Exception) {
					return result
				}
			}
			return result
		}
	}
}