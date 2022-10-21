package com.pvcombank.sdk.ekyc.network

import com.pvcombank.sdk.ekyc.model.ResponseData
import com.pvcombank.sdk.ekyc.model.TransactionHistoryModel
import com.pvcombank.sdk.ekyc.model.request.*
import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody
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
	
	@POST("staging/v1/prepaid-cards/transaction-history")
	fun getTransactionHistory(@Body request: RequestTransactionHistory): Observable<ResponseData<List<TransactionHistoryModel>>>
	
	@POST("staging/v1/prepaid-cards/reset-pin")
	fun resetPin(@Body request: RequestResetPin): Observable<Any>
	
	@POST("staging/v1/prepaid-cards/card-lock")
	fun lockCard(@Body request: RequestLockCard): Observable<ResponseBody>
	
	@POST("staging/v1/prepaid-cards")
	fun unlockCard(@Body request: RequestUnlockCard): Observable<ResponseBody>
	
}