package com.pvcombank.sdk.payment.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.pvcombank.sdk.payment.model.request.AuthToken
import com.pvcombank.sdk.payment.model.request.RequestCreateAccount
import com.pvcombank.sdk.payment.model.request.RequestModel
import com.pvcombank.sdk.payment.model.request.RequestPurchase
import com.pvcombank.sdk.payment.model.response.ResponsePurchase
import com.pvcombank.sdk.payment.model.response.ResponseVerifyOTP
import com.pvcombank.sdk.payment.model.response.ResponseVerifyOnboardOTP
import com.pvcombank.sdk.payment.network.ApiHelper
import com.pvcombank.sdk.payment.network.ApiOther
import com.pvcombank.sdk.payment.network.RetrofitHelper
import com.pvcombank.sdk.payment.util.NetworkUtil.getErrorBody
import com.pvcombank.sdk.payment.util.Utils.toObjectData
import com.pvcombank.sdk.payment.util.security.SecurityHelper
import com.google.gson.Gson
import com.pvcombank.sdk.BuildConfig
import com.pvcombank.sdk.payment.model.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class AuthRepository() {
	private val apiHelper: ApiHelper = RetrofitHelper.instance()
		.createServices(Constants.BASE_URL)
		.create(ApiHelper::class.java)
	
	private val apiOther: ApiOther = RetrofitHelper.instance()
		.createServices(BuildConfig.SERVER_URL)
		.create(ApiOther::class.java)
	
	private val masterData = MasterModel.getInstance()
	
	val onNeedLogin = MutableLiveData<RequestModel>()
	private val securityHelper = SecurityHelper.instance().cryptoBuild(type = SecurityHelper.AES)
	
	fun getTokenByCode(
		code: String,
		clientId: String,
		clientSecret: String,
		callBack: (GetAccessTokenModel?) -> Unit
	) {
		apiHelper.apply {
			this.getAccessToken(code = code, clientId = clientId, clientSecret = clientSecret)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.subscribe(
					{
						callBack.invoke(it)
					},
					{
						callBack.invoke(null)
						Log.e("ERROR", it.message.toString())
					}
				)
		}
	}
	
	fun checkAccessToken(request: AuthToken, callBack: (Any) -> Unit) {
		apiHelper.apply {
			this.checkAccessToken(request)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.subscribe(
					{
						callBack.invoke(it)
					},
					{
						Log.e("ERROR", it.message.toString())
					}
				)
		}
	}
	
	fun getListCard(callBack: (List<CardModel>?) -> Unit) {
		apiOther.apply {
			this.getListCardDetail()
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.subscribe(
					{
						if (it.code == "401") onNeedLogin.postValue(it)
						val decryptText = securityHelper?.decrypt(it.data ?: "")
						decryptText?.toObjectData<ResponseData<List<CardModel>>>()?.let {
							if (it.code == Constants.CODE_SUCCESS && it.data != null) {
								callBack.invoke(it.data)
							}
						}
					},
					{
						Log.d("ERROR", "${it.message}")
						it.getErrorBody()?.let { requestModel ->
							if (requestModel.code == "401") onNeedLogin.postValue(requestModel)
						}
						callBack.invoke(null)
					}
				)
		}
	}
	
	fun getCard(cardToken: String, callBack: (Any?) -> Unit) {
		apiOther.getCardDetail(cardToken)
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeOn(Schedulers.io())
			.subscribe(
				{
					val decryptText = securityHelper?.decrypt(it.data ?: "")
					decryptText?.toObjectData<ResponseData<CardModel>>()?.let {
						if (it.code == Constants.CODE_SUCCESS) {
							callBack.invoke(it.data)
						}
					}
				}, {
					it.getErrorBody()?.let { requestModel ->
						if (requestModel.code == "401") onNeedLogin.postValue(requestModel)
						requestModel.data?.apply {
							callBack.invoke(securityHelper?.decrypt(this))
						}
					} ?: kotlin.run {
						callBack.invoke(it.message.toString())
					}
				}
			)
	}
	
	fun purchase(cardToken: String, callBack: (Any) -> Unit) {
		apiOther.apply {
			val request = RequestPurchase(
				amount = masterData.orderCurrency?.toLong() ?: 0L,
				description = masterData.orderDesc,
				traceNumber = masterData.idOrder,
				cardToken = cardToken
			)
			val requestModel = RequestModel()
			securityHelper?.encrypt(Gson().toJson(request))?.apply {
				requestModel.data = this
				purchase(request = requestModel)
					.observeOn(AndroidSchedulers.mainThread())
					.subscribeOn(Schedulers.io())
					.subscribe(
						{
							val decryptText = securityHelper.decrypt(it.data ?: "")
							decryptText.toObjectData<ResponseData<ResponsePurchase>>()?.let {
								if (it.code == Constants.CODE_SUCCESS && it.data != null) {
									callBack.invoke(it.data!!)
								}
							}
						},
						{
							it.getErrorBody()?.let { requestModel ->
								if (requestModel.code == "401") onNeedLogin.postValue(requestModel)
								requestModel.data?.apply {
									callBack.invoke(securityHelper.decrypt(this))
								}
							} ?: kotlin.run {
								callBack.invoke(it.message.toString())
							}
						}
					)
			}
		}
	}
	
	fun sendOTP(phoneNumber: String, email: String, callBack: (Any) -> Unit) {
		val request = RequestCreateAccount(email, phoneNumber)
		val stringEncrypt = SecurityHelper.instance()
			.cryptoBuild(type = SecurityHelper.AES)
			?.encrypt(Gson().toJson(request))
		apiOther.apply {
			this.sendOTP(RequestModel(data = stringEncrypt))
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.subscribe(
					{
						val decryptText = securityHelper?.decrypt(it.data ?: "")
						decryptText?.toObjectData<ResponseData<ResponsePurchase>>()?.let {
							if (it.code == Constants.CODE_SUCCESS && it.data != null) {
								callBack.invoke(it.data!!)
								MasterModel.getInstance().uuidOfOTP = it.data?.uuid
							} else {
								callBack.invoke("Đã có lỗi, vui lòng thử lại")
							}
						}
					},
					{
						it.getErrorBody()?.let { requestModel ->
							if (requestModel.code == "401") onNeedLogin.postValue(requestModel)
							requestModel.data?.apply {
								callBack.invoke(securityHelper!!.decrypt(this))
							}
						} ?: kotlin.run {
							callBack.invoke(it.message.toString())
						}
					}
				)
		}
	}
	
	fun verifyOnboardOTP(request: RequestModel, callBack: (Any) -> Unit){
		apiOther.apply {
			this.verifyOnboardOTP(request =request)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.subscribe(
					{
						val decryptText = securityHelper?.decrypt(it.data ?: "")
						decryptText?.toObjectData<ResponseData<ResponseVerifyOnboardOTP>>()?.let {
							if (it.code == Constants.CODE_SUCCESS && it.data != null) {
								callBack.invoke(it.data!!)
							} else {
								callBack.invoke("Đã có lỗi, vui lòng thử lại")
							}
						}
					},
					{
						val error = RequestModel()
						it.getErrorBody()?.let { requestErrorBody ->
							if (requestErrorBody.code == "401") {
								onNeedLogin.postValue(
									requestErrorBody
								)
							} else {
								if (requestErrorBody.data == null) {
									callBack.invoke(error)
								} else {
									SecurityHelper.instance()
										.cryptoBuild(type = SecurityHelper.AES)
										?.decrypt(requestErrorBody.data)
										?.toObjectData<ResponseData<ErrorModel>>()
										?.let { modelError ->
											error.code = modelError.code
											error.message = modelError.message
											callBack.invoke(error)
										} ?: kotlin.run {
										callBack.invoke(error)
									}
								}
							}
						} ?: kotlin.run {
							error.message = it.message
							callBack.invoke(error)
						}
					}
				)
		}
	}
	fun verifyOTP(request: RequestModel, callBack: (Any) -> Unit) {
		apiOther.apply {
			this.verifyOTP(request)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.subscribe(
					{
						val decryptText = securityHelper?.decrypt(it.data ?: "")
						decryptText?.toObjectData<ResponseData<ResponseVerifyOTP>>()?.let {
							if (it.code == Constants.CODE_SUCCESS && it.data != null) {
								callBack.invoke(it.data!!)
							} else {
								callBack.invoke("Đã có lỗi, vui lòng thử lại")
							}
						}
					},
					{
						val error = RequestModel()
						it.getErrorBody()?.let { requestErrorBody ->
							if (requestErrorBody.code == "401") {
								onNeedLogin.postValue(
									requestErrorBody
								)
							} else {
								if (requestErrorBody.data == null) {
									callBack.invoke(error)
								} else {
									SecurityHelper.instance()
										.cryptoBuild(type = SecurityHelper.AES)
										?.decrypt(requestErrorBody.data)
										?.toObjectData<ResponseData<ErrorModel>>()
										?.let { modelError ->
											error.code = modelError.code
											error.message = modelError.message
											callBack.invoke(error)
										} ?: kotlin.run {
										callBack.invoke(error)
									}
								}
							}
						} ?: kotlin.run {
							error.message = it.message
							callBack.invoke(error)
						}
					}
				)
		}
	}
}