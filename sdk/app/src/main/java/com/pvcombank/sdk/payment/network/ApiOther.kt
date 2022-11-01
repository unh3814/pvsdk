package com.pvcombank.sdk.payment.network

import com.pvcombank.sdk.payment.model.request.*
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiOther {
	@POST("staging/v1/prepaid-cards/purchase")
	fun purchase(
		@Body request: RequestModel
	): Observable<RequestModel>
	
	@POST("staging/v1/user/verify-otp")
	fun verifyOTP(@Body request: RequestModel): Observable<RequestModel>
	
	@POST("staging/v1/onboarding/verify-otp")
	fun verifyOnboardOTP(@Body request: RequestModel): Observable<RequestModel>
	
	@POST("staging/v1/onboarding/send-otp")
	fun sendOTP(@Body request: RequestModel): Observable<RequestModel>
	
	@GET("staging/v1/prepaid-cards/{id}")
	fun getCardDetail(@Path("id") id: String): Observable<RequestModel>
	
	@GET("staging/v1/prepaid-cards/list-card")
	fun getListCardDetail(): Observable<RequestModel>
}