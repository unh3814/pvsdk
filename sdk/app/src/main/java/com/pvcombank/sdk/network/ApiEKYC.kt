package com.pvcombank.sdk.network

import com.pvcombank.sdk.model.request.RequestVerifySelfies
import com.pvcombank.sdk.model.response.ResponseOCR
import io.reactivex.rxjava3.core.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiEKYC {
	@POST("verify")
	fun verifyCard(@Body request: RequestBody): Observable<ResponseOCR>
	
	@POST("verify-selfie")
	fun verifySelfie(
		@Body request: RequestVerifySelfies
	): Observable<ResponseOCR>
}