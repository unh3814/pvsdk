package com.pvcombank.sdk.repository

import androidx.lifecycle.MutableLiveData
import com.pvcombank.sdk.ekyc.BuildConfig
import com.pvcombank.sdk.network.API
import com.pvcombank.sdk.network.RetrofitHelper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class ApiRepository : MyBaseRepository() {
	private val service = RetrofitHelper.instance()
		.createServices(BuildConfig.SERVER_URL)
		.create(API::class.java)

	val observableAPI = MutableLiveData<Any>()

	fun getAPI(){
		service.getAPI()
			.observeOn(Schedulers.io())
			.subscribeOn(AndroidSchedulers.mainThread())
			.subscribe(
				{handlerSuccess(it, observableAPI)},
				{handlerError(it)}
			)
	}

	override fun clear() {
		TODO("Not yet implemented")
	}
}