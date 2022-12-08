package com.pvcombank.sdk.payment.network

import com.pvcombank.sdk.payment.model.CardModel
import com.pvcombank.sdk.payment.model.Constants
import com.pvcombank.sdk.payment.model.ResponseData
import com.pvcombank.sdk.payment.model.request.*
import com.pvcombank.sdk.payment.model.response.ResponsePurchase
import com.pvcombank.sdk.payment.model.response.ResponseVerifyOTP
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.*

interface ApiOther {
	@POST(Constants.API_PURCHASE)
	fun purchase(
		@Body request: RequestPurchase
	): Observable<ResponseData<ResponsePurchase>>
	
	@POST(Constants.API_VERIFY_OTP)
	fun verifyOTP(@Body request: RequestVerifyOTP): Observable<ResponseData<ResponseVerifyOTP>>

	@GET(Constants.API_CARD_DETAIL)
	fun getCardDetail(@Path("id") id: String): Observable<ResponseData<CardModel>>
	
	@GET(Constants.API_LIST_CARD)
	fun getListCardDetail(): Observable<ResponseData<List<CardModel>>>

	@GET(Constants.API_METHODS)
	fun getMethods(): Observable<ResponseData<List<CardModel>>>

	@GET(Constants.API_METHODS_DETAIL)
	fun getMethodsDetail(
		@Query("type") type: String,
		@Query("source") source: String
	): Observable<ResponseData<CardModel>>
}