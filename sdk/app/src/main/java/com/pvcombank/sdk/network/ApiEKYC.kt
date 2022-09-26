package com.pvcombank.sdk.network

import com.pvcombank.sdk.model.response.ResponseOCR
import io.reactivex.rxjava3.core.Observable
import okhttp3.MultipartBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiEKYC {
	@FormUrlEncoded
	@POST("verify-card")
	fun verifyCard(
		@Field("file") file: MultipartBody.Part,
		@Field("label") label: String
	): Observable<ResponseOCR>
	
	@FormUrlEncoded
	@POST("verify")
	fun verifyCard(
		@Field("files") files: List<MultipartBody.Part>,
		@Field("label") label: String
	): Observable<ResponseOCR>
	
	@FormUrlEncoded
	@POST("verify-selfie")
	fun verifySelfie(): Observable<ResponseOCR>
}