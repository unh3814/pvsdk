package com.pvcombank.sdk.repository

import android.util.Log
import com.pvcombank.sdk.BuildConfig
import com.pvcombank.sdk.model.request.RequestVerifySelfies
import com.pvcombank.sdk.model.response.ResponseOCR
import com.pvcombank.sdk.network.ApiEKYC
import com.pvcombank.sdk.network.RetrofitHelper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody

class OnBoardingRepository {
	private val apiServices: ApiEKYC = RetrofitHelper.instance()
		.createServices(BuildConfig.ONBOARDING_URL)
		.create(ApiEKYC::class.java)
	
	fun verifyCard(
		requestBody: RequestBody,
		callBack: (HashMap<String, Any>) -> Unit
	) {
		val result = hashMapOf<String, Any>()
		apiServices.verifyCard(requestBody).observeOn(AndroidSchedulers.mainThread())
			.subscribeOn(Schedulers.io())
			.subscribe(
				{
					Log.d("Verify", "Success")
					result["success"] = it
					callBack.invoke(result)
				},
				{
					Log.d("Verify", "Fail")
					result["error"] = it.message.toString()
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
					if (it.error?.isNotEmpty() == true) {
						result["fail"] = it.error ?: ""
					} else {
						result["success"] = it
					}
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
}