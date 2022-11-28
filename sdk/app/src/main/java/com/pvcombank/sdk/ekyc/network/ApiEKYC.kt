package com.pvcombank.sdk.ekyc.network

import com.pvcombank.sdk.ekyc.model.Constants
import com.pvcombank.sdk.ekyc.model.ResponseData
import com.pvcombank.sdk.ekyc.model.request.*
import com.pvcombank.sdk.ekyc.model.response.CheckAccountResponse
import com.pvcombank.sdk.ekyc.model.response.ResponseOCR
import io.reactivex.rxjava3.core.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiEKYC {
	@POST(Constants.API_VERIFY)
	fun verifyCard(@Body request: RequestBody): Observable<ResponseOCR>
	
	@POST(Constants.API_VERIFY_SELFIE)
	fun verifySelfie(
		@Body request: RequestVerifySelfies
	): Observable<ResponseOCR>
	
	@POST(Constants.API_FINISH)
	fun finish(@Body request: RequestFinish): Observable<ResponseData<Any>>
	
	@PUT(Constants.API_UPDATE_PASSWORD)
	fun updatePassword(@Body request: RequestModel): Observable<RequestModel>
	
	@POST(Constants.API_CHECK_ACC_BANKING)
	fun checkAccount(@Body request: CheckAccountRequest): Observable<CheckAccountResponse>
}