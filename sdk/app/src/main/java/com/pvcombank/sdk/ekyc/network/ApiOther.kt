package com.pvcombank.sdk.ekyc.network

import com.pvcombank.sdk.ekyc.model.Constants
import com.pvcombank.sdk.ekyc.model.request.*
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiOther {
	
	@POST(Constants.API_VERIFY_OTP)
	fun verifyOnboardOTP(@Body request: RequestModel): Observable<RequestModel>
	
	@POST(Constants.API_SEND_OTP)
	fun sendOTP(@Body request: RequestModel): Observable<RequestModel>
}