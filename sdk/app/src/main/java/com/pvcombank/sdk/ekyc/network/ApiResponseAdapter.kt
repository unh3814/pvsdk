package com.pvcombank.sdk.ekyc.network

import com.pvcombank.sdk.ekyc.model.ResponseData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Converter
import java.lang.reflect.Type

class ApiResponseAdapter<S : Any>(
	private val successType: Type,
	private val errorConverter: Converter<ResponseBody, ResponseData<Any>>
): CallAdapter<S, Call<ApiResponse<S>>> {
	override fun responseType(): Type = successType
	
	override fun adapt(call: Call<S>): Call<ApiResponse<S>> {
		return ApiResponseCall(call, errorConverter)
	}
}