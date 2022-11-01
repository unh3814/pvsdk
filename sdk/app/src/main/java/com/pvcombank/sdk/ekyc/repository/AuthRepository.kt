package com.pvcombank.sdk.ekyc.repository

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.pvcombank.sdk.ekyc.BuildConfig
import com.pvcombank.sdk.ekyc.model.Constants
import com.pvcombank.sdk.ekyc.model.GetAccessTokenModel
import com.pvcombank.sdk.ekyc.model.request.AuthToken
import com.pvcombank.sdk.ekyc.model.request.RequestCreateAccount
import com.pvcombank.sdk.ekyc.model.request.RequestModel
import com.pvcombank.sdk.ekyc.model.response.ResponsePurchase
import com.pvcombank.sdk.ekyc.model.response.ResponseVerifyOnboardOTP
import com.pvcombank.sdk.ekyc.network.ApiHelper
import com.pvcombank.sdk.ekyc.network.ApiOther
import com.pvcombank.sdk.ekyc.network.RetrofitHelper
import com.pvcombank.sdk.ekyc.util.Utils.toObjectData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class AuthRepository : PVRepository(), HandlerData {
	private val apiHelper: ApiHelper = RetrofitHelper.instance()
		.createServices(BuildConfig.SERVER_URL)
		.create(ApiHelper::class.java)
	
	private val apiOther: ApiOther = RetrofitHelper.instance()
		.createServices(Constants.BASE_URL_OTHER)
		.create(ApiOther::class.java)
	
	val observerSendOTPResponse = MutableLiveData<ResponsePurchase>()
	val observerVerifyOTP = MutableLiveData<ResponseVerifyOnboardOTP>()
	val observerGetToken = MutableLiveData<GetAccessTokenModel>()
	
	fun getTokenByCode(
		code: String,
		clientId: String,
		clientSecret: String
	) {
		apiHelper.apply {
			this.getAccessToken(
				code = code,
				clientId = clientId,
				clientSecret = clientSecret
			)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.subscribe(
					{ handlerSuccess<GetAccessTokenModel>(this@AuthRepository, it, Constants.API_GET_TOKEN) },
					{ handlerError(it) }
				)
		}
	}
	
	fun checkAccessToken(request: AuthToken) {
		apiHelper.apply {
			this.checkAccessToken(request)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.subscribe(
					{ handlerSuccess<GetAccessTokenModel>(this@AuthRepository, it, Constants.API_REFRESH_TOKEN) },
					{ handlerError(it) }
				)
		}
	}
	
	fun sendOTP(phoneNumber: String, email: String) {
		val request = encryptRequest(
			RequestCreateAccount(email, phoneNumber)
		)
		apiOther.apply {
			this.sendOTP(request)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.subscribe(
					{ handlerSuccess<ResponsePurchase>(this@AuthRepository, it, Constants.API_SEND_OTP) },
					{ handlerError(it) }
				)
		}
	}
	
	fun verifyOnboardOTP(request: RequestModel) {
		apiOther.apply {
			this.verifyOnboardOTP(request = request)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.subscribe(
					{ handlerSuccess<ResponseVerifyOnboardOTP>(this@AuthRepository, it, Constants.API_VERIFY_OTP) },
					{ handlerError(it) }
				)
		}
	}
	
	override fun onDataSuccess(api: String, data: Any?) {
		when (api) {
			Constants.API_SEND_OTP -> {
				observerSendOTPResponse.postValue(
					Gson().toJson(data).toObjectData()
				)
			}
			Constants.API_VERIFY_OTP -> {
				observerVerifyOTP.postValue(
					Gson().toJson(data).toObjectData()
				)
			}
			Constants.API_REFRESH_TOKEN, Constants.API_GET_TOKEN -> {
				(data as? GetAccessTokenModel)?.apply {
					Constants.TOKEN = "$tokenType $accessToken"
					Constants.TOKEN = "$tokenType $refreshToken"
					observerGetToken.postValue(this)
				}
			}
		}
	}
	
	override fun clear() {
		observerSendOTPResponse.postValue(null)
		observerVerifyOTP.postValue(null)
		observerGetToken.postValue(null)
		error.postValue(null)
	}
}