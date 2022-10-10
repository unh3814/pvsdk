package com.pvcombank.sdk.network

import com.pvcombank.sdk.model.ResponseData
import java.io.IOException

sealed class ApiResponse<out O: Any> {
	data class Success<T : Any>(val code: String, val body: T) : ApiResponse<T>()
	data class ApiError(val code: String, val error: ResponseData<Any>) : ApiResponse<Nothing>()
	data class NetworkError(val error: IOException) : ApiResponse<Nothing>()
	data class UnknownError(val body: Throwable?) : ApiResponse<Nothing>()
}
