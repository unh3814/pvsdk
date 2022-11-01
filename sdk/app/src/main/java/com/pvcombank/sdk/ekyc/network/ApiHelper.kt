package com.pvcombank.sdk.ekyc.network

import com.pvcombank.sdk.ekyc.model.Constants
import com.pvcombank.sdk.ekyc.model.GetAccessTokenModel
import com.pvcombank.sdk.ekyc.model.ResponseData
import com.pvcombank.sdk.ekyc.model.request.AuthToken
import io.reactivex.rxjava3.core.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiHelper {
	@FormUrlEncoded
	@POST(Constants.API_GET_TOKEN)
	fun getAccessToken(
		@Field("client_id") clientId: String,
		@Field("grant_type") grantType: String? = Constants.GRANT_TYPE_CODE,
		@Field("client_secret") clientSecret: String,
		@Field("code") code: String,
		@Field("redirect_uri") redirectUri: String? = Constants.REDIRECT_SANBOX_URL
	): Observable<GetAccessTokenModel>
	
	@FormUrlEncoded
	@POST("auth/realms/pvcombank/protocol/openid-connect/token")
	fun newToken(
		@Field("client_id") clientId: String,
		@Field("grant_type") grantType: String? = Constants.GRANT_TYPE_CODE,
		@Field("client_secret") clientSecret: String,
		@Field("code") code: String,
		@Field("redirect_uri") redirectUri: String? = Constants.REDIRECT_URL
	): Response<ResponseData<GetAccessTokenModel>>
	
	
	@POST(Constants.API_REFRESH_TOKEN)
	fun checkAccessToken(@Body request: AuthToken): Observable<Any>
}