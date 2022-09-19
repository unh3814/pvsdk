package com.pvcombank.sdk.network

import com.pvcombank.sdk.model.CardInfor
import io.reactivex.rxjava3.core.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.Part

interface ApiEKYC {
	
	@Multipart
	@GET("/v3.2/ocr/recognition")
	fun ocrCard(
		@Header("key") apiKey : String,
		@Part image: MultipartBody.Part,
		@Part("request_id") requestId: RequestBody
	) : Observable<CardInfor>
}