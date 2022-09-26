package com.pvcombank.sdk.repository

import android.util.Log
import com.pvcombank.sdk.BuildConfig
import com.pvcombank.sdk.network.ApiEKYC
import com.pvcombank.sdk.network.RetrofitHelper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MultipartBody
import java.util.*
import kotlin.collections.HashMap

class OnBoardingRepository {
	private val apiServices: ApiEKYC = RetrofitHelper.instance()
		.createServices(BuildConfig.ONBOARDING_URL)
		.create(ApiEKYC::class.java)
	
	fun verifyCard(
		multipart: MultipartBody.Part,
		type: String,
		callBack: (HashMap<String, Any>) -> Unit
	) {
		val result = hashMapOf<String, Any>()
		apiServices.verifyCard(
			file = multipart,
			label = type
		).observeOn(AndroidSchedulers.mainThread())
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
	
	fun verifyBoldCard(
		multiparts: List<MultipartBody.Part>,
		callBack: (HashMap<String, Any>) -> Unit
	) {
		val result = hashMapOf<String, Any>()
		apiServices.verifyCard(
			files = multiparts,
			label = ""
		).observeOn(AndroidSchedulers.mainThread())
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
	
	fun verifySelfies(gesture: List<Base64>, frontal: List<Base64>, videos: List<Any>) {
	
	}
}