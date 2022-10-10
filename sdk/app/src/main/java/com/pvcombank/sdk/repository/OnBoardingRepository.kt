package com.pvcombank.sdk.repository

import com.pvcombank.sdk.BuildConfig
import com.pvcombank.sdk.model.request.RequestFinish
import com.pvcombank.sdk.model.request.RequestUpdatePassword
import com.pvcombank.sdk.model.request.RequestVerifySelfies
import com.pvcombank.sdk.network.ApiEKYC
import com.pvcombank.sdk.network.ApiResponse
import com.pvcombank.sdk.network.RetrofitHelper
import com.pvcombank.sdk.util.execute.ThreadExecutor
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.RequestBody
import java.util.concurrent.Executors

class OnBoardingRepository {
	private val executeFactory = ThreadExecutor.Default.build()
	private val apiServices: ApiEKYC = RetrofitHelper.instance()
		.createServices(BuildConfig.ONBOARDING_URL)
		.create(ApiEKYC::class.java)
	private val newApiServices: ApiEKYC = RetrofitHelper.instance()
		.createNewServices(BuildConfig.ONBOARDING_URL)
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
	
	fun updatePassword(password: String) {
		executeFactory.executeIO().execute {
			newApiServices.updatePassword(
				RequestUpdatePassword(password)
			).apply {
				when (this) {
					is ApiResponse.ApiError -> {}
					is ApiResponse.NetworkError -> {}
					is ApiResponse.UnknownError -> {}
					is ApiResponse.Success -> {}
				}
			}
		}
	}
}