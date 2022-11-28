package com.pvcombank.sdk.ekyc.util

import com.pvcombank.sdk.ekyc.model.request.RequestModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.HttpException

object NetworkUtil {
	private val gson = Gson()
	fun Throwable.getErrorBody(): RequestModel? {
		return (this as? HttpException)?.response()?.let { resonse ->
			val typeResponse = object : TypeToken<RequestModel>() {}.type
			val result = RequestModel()
			result.code = resonse.code().toString()
			resonse.errorBody()?.let {
				try {
					val model = gson.fromJson<RequestModel>(it.string(), typeResponse)
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