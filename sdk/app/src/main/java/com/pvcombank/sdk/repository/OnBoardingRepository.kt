package com.pvcombank.sdk.repository

import com.google.gson.Gson
import com.pvcombank.sdk.BuildConfig
import com.pvcombank.sdk.model.Constants
import com.pvcombank.sdk.model.MasterModel
import com.pvcombank.sdk.model.ResponseData
import com.pvcombank.sdk.model.request.*
import com.pvcombank.sdk.model.response.ResponsePurchase
import com.pvcombank.sdk.network.ApiEKYC
import com.pvcombank.sdk.network.RetrofitHelper
import com.pvcombank.sdk.util.Utils.toObjectData
import com.pvcombank.sdk.util.security.SecurityHelper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.RequestBody

class OnBoardingRepository {
	private val apiServices: ApiEKYC = RetrofitHelper.instance()
		.createServices(BuildConfig.ONBOARDING_URL)
		.create(ApiEKYC::class.java)
	private val retrofitUpdatePassword = RetrofitHelper.instance()
		.createServices("https://iuhfhsds3h.execute-api.ap-southeast-1.amazonaws.com/staging/v1/")
		.create(ApiEKYC::class.java)
	private val securityHelper = SecurityHelper.instance().cryptoBuild(type = SecurityHelper.AES)
	
	fun verifyCard(
		requestBody: RequestBody,
		callBack: (HashMap<String, Any>) -> Unit
	) {
		val result = hashMapOf<String, Any>()
		apiServices.verifyCard(requestBody).observeOn(AndroidSchedulers.mainThread())
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
	
	fun finish(requestFinish: RequestFinish, callBack: (HashMap<String, Any>) -> Unit) {
		val result = hashMapOf<String, Any>()
		apiServices.finish(requestFinish)
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
	
	fun updatePassword(password: String, callBack: (HashMap<String, Any>) -> Unit) {
		val result = hashMapOf<String, Any>()
		val requestRaw = RequestUpdatePassword(password)
		val stringEncrypt = SecurityHelper.instance()
			.cryptoBuild(type = SecurityHelper.AES)
			?.encrypt(Gson().toJson(requestRaw))
		
		retrofitUpdatePassword.updatePassword(
			RequestModel(data = stringEncrypt)
		).observeOn(Schedulers.io())
			.subscribeOn(AndroidSchedulers.mainThread())
			.subscribe(
				{
					val decryptText = securityHelper?.decrypt(it.data ?: "")
					decryptText?.toObjectData<ResponseData<Any>>()?.let {
						if (it.code == Constants.CODE_SUCCESS) {
							result["success"] = it
						} else {
							result["fail"] = it.message ?: "Đã có lỗi, vui lòng thử lại"
						}
						callBack.invoke(
							result
						)
					}
				},
				{
					result["fail"] = it.message.toString()
					callBack.invoke(
						result
					)
				}
			)
	}
}