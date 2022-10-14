package com.pvcombank.sdk.network

import com.pvcombank.sdk.model.ResponseData
import com.pvcombank.sdk.model.request.RequestFinish
import com.pvcombank.sdk.model.request.RequestModel
import com.pvcombank.sdk.model.request.RequestUpdatePassword
import com.pvcombank.sdk.model.request.RequestVerifySelfies
import com.pvcombank.sdk.model.response.ResponseOCR
import io.reactivex.rxjava3.core.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiEKYC {
	@POST("verify")
	fun verifyCard(@Body request: RequestBody): Observable<ResponseOCR>
	
	@POST("verify-selfie")
	fun verifySelfie(
		@Body request: RequestVerifySelfies
	): Observable<ResponseOCR>
	
	@POST("ekyc/finish")
	fun finish(@Body request: RequestFinish): Observable<ResponseData<Any>>
	
	@PUT("onboarding/update-password")
	fun updatePassword(@Body request: RequestModel): Observable<RequestModel>
}