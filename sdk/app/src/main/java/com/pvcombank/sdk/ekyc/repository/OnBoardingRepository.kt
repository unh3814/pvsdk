package com.pvcombank.sdk.ekyc.repository

import androidx.lifecycle.MutableLiveData
import com.pvcombank.sdk.ekyc.BuildConfig
import com.pvcombank.sdk.ekyc.model.Constants
import com.pvcombank.sdk.ekyc.model.ResponseData
import com.pvcombank.sdk.ekyc.model.request.CheckAccountRequest
import com.pvcombank.sdk.ekyc.model.request.RequestFinish
import com.pvcombank.sdk.ekyc.model.request.RequestUpdatePassword
import com.pvcombank.sdk.ekyc.model.request.RequestVerifySelfies
import com.pvcombank.sdk.ekyc.model.response.CheckAccountResponse
import com.pvcombank.sdk.ekyc.network.ApiEKYC
import com.pvcombank.sdk.ekyc.network.RetrofitHelper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.RequestBody

class OnBoardingRepository : PVRepository(), HandlerData {
	private val apiServices: ApiEKYC = RetrofitHelper.instance()
		.createServices(BuildConfig.ONBOARDING_URL)
		.create(ApiEKYC::class.java)
	private val retrofitUpdatePassword = RetrofitHelper.instance()
		.createServices("https://iuhfhsds3h.execute-api.ap-southeast-1.amazonaws.com/staging/v1/")
		.create(ApiEKYC::class.java)
	private val retrofitCheckAccount = RetrofitHelper.instance()
		.createServices("https://mobile25uat.pvcombank.com.vn/api/")
		//.createServices("https://mb25mfatest.pvcombank.com.vn/api/")
		.create(ApiEKYC::class.java)
	val observerUpdatePassword = MutableLiveData<String>()
	val observerFinish = MutableLiveData<ResponseData<*>>()
	fun verifyCard(
		requestBody: RequestBody,
		callBack: (HashMap<String, Any>) -> Unit
	) {
		val result = hashMapOf<String, Any>()
		apiServices.verifyCard(requestBody)
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeOn(Schedulers.io())
			.subscribe(
				{
					result["success"] = it
					callBack.invoke(result)
				},
				{
					result["fail"] = it.message.toString()
					callBack.invoke(result)
				}
			)
	}
	
	fun verifySelfies(
		requestVerifySelfies: RequestVerifySelfies,
		callBack: (HashMap<String, Any>) -> Unit
	) {
		val result = hashMapOf<String, Any>()
		apiServices.verifySelfie(requestVerifySelfies)
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeOn(Schedulers.io())
			.subscribe(
				{
					result["success"] = it
					callBack.invoke(
						result
					)
				},
				{
					result["fail"] = it.message.toString()
					callBack.invoke(
						result
					)
				}
			)
	}
	
	fun finish(requestFinish: RequestFinish) {
		apiServices.finish(requestFinish)
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeOn(Schedulers.io())
			.subscribe(
				{ handlerSuccess<Any>(this, it, Constants.API_FINISH) },
				{ handlerError(it) }
			)
	}
	
	fun checkAccount(request: CheckAccountRequest, callBack: (HashMap<String, Any>) -> Unit) {
		val result = hashMapOf<String, Any>()
		retrofitCheckAccount.checkAccount(request)
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeOn(Schedulers.io())
			.subscribe(
				{
					if (it.C == "001") {
						if (it.E?.status == "1") {
							//Dừng lại
							result["stop"] = it.E.message ?: ""
						}
						if (it.E?.status == "2") {
							//Đi tiếp
							result["next"] = it.E.message ?: ""
						}
					} else if (it.C == "999") {
						result["error_network"] = "Gọi thất bại"
					} else {
						result["fail"] = it.E?.message ?: ""
					}
					callBack.invoke(result)
				},
				{
					result["error_network"] = "Đã có lỗi vui lòng thử lại."
					callBack.invoke(result)
				}
			)
	}
	
	fun updatePassword(password: String) {
		val value = encryptRequest(RequestUpdatePassword(password))
		retrofitUpdatePassword.updatePassword(
			value
		).observeOn(AndroidSchedulers.mainThread())
			.subscribeOn(Schedulers.io())
			.subscribe(
				{ handlerSuccess<Any>(this, it, Constants.API_UPDATE_PASSWORD) },
				{ handlerError(it) }
			)
	}
	
	override fun clear() {
		error.postValue(null)
		observerFinish.postValue(null)
		observerUpdatePassword.postValue(null)
	}
	
	override fun onDataSuccess(api: String, data: Any?) {
		when (api) {
			Constants.API_UPDATE_PASSWORD -> {
				observerUpdatePassword.postValue("success")
			}
			Constants.API_FINISH -> {
				observerFinish.postValue(data as? ResponseData<*>)
			}
		}
	}
}