package com.pvcombank.sdk.network

import com.pvcombank.sdk.model.ResponseData
import okhttp3.Request
import okhttp3.ResponseBody
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import java.io.IOException

internal class ApiResponseCall<S : Any>(
	private val delegate: Call<S>,
	private val errorConverter: Converter<ResponseBody, ResponseData<Any>>
	) : Call<ApiResponse<S>> {
	override fun clone(): Call<ApiResponse<S>> = ApiResponseCall(delegate.clone(), errorConverter)
	
	override fun execute(): Response<ApiResponse<S>> {
		throw  UnsupportedOperationException("ApiResponseCall doesn't support execute")
	}
	
	override fun enqueue(callback: Callback<ApiResponse<S>>) = delegate.enqueue(
		object : Callback<S> {
			override fun onResponse(call: Call<S>, response: Response<S>) {
				val body = response.body()
				val code = response.code()
				val error = response.errorBody()
				if (response.isSuccessful) {
					if (body != null) {
						callback.onResponse(
							this@ApiResponseCall,
							Response.success(ApiResponse.Success(code.toString(), body))
						)
					} else {
						callback.onResponse(
							this@ApiResponseCall,
							Response.success(ApiResponse.UnknownError(null))
						)
					}
				} else {
					val errorBody = when {
						error == null -> null
						error.contentLength() == 0L -> null
						else -> try {
							errorConverter.convert(error)
						} catch (ex: Exception) {
							null
						}
					}
					if (errorBody != null) {
						callback.onResponse(
							this@ApiResponseCall,
							Response.success(
								ApiResponse.ApiError(code.toString(), errorBody)
							)
						)
					} else {
						callback.onResponse(
							this@ApiResponseCall,
							Response.success(ApiResponse.UnknownError(null))
						)
					}
				}
			}
			
			override fun onFailure(call: Call<S>, t: Throwable) {
				val apiResponse = when (t) {
					is IOException -> ApiResponse.NetworkError(t)
					else -> ApiResponse.UnknownError(t)
				}
				callback.onResponse(
					this@ApiResponseCall,
					Response.success(apiResponse)
				)
			}
		}
	)
	
	override fun isExecuted(): Boolean = delegate.isExecuted
	
	override fun cancel() = delegate.cancel()
	
	override fun isCanceled(): Boolean = delegate.isCanceled
	
	override fun request(): Request = delegate.request()
	
	override fun timeout(): Timeout = delegate.timeout()
}