package com.pvcombank.sdk.network

import okhttp3.ResponseBody
import retrofit2.Response

sealed class ResultAPI<T> {
	data class Success<R>(var response: Response<R>): ResultAPI<R>()
	data class Error<R>(var respnse: ResponseBody?): ResultAPI<R>()
}