package com.pvcombank.sdk.ekyc.repository

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import com.pvcombank.sdk.ekyc.model.Constants
import com.pvcombank.sdk.ekyc.model.ErrorModel
import com.pvcombank.sdk.ekyc.model.ResponseData
import com.pvcombank.sdk.ekyc.model.request.RequestModel
import com.pvcombank.sdk.ekyc.util.NetworkUtil.getErrorBody
import com.pvcombank.sdk.ekyc.util.Utils.toObjectData
import com.pvcombank.sdk.ekyc.util.execute.MyExecutor
import com.pvcombank.sdk.ekyc.util.security.SecurityHelper
import java.io.IOException

abstract class PVRepository {
	private val securityHelper = SecurityHelper.instance().cryptoBuild(type = SecurityHelper.AES)
	val error = MutableLiveData<Pair<Int, String>>()
	val gson get() = GsonBuilder().create()
	val executor = MyExecutor.Default.build().executeDefault()
	
	fun encryptRequest(value: Any): RequestModel {
		val jsonStr = gson.toJson(value)
		val dataEncrypt = SecurityHelper.instance()
			.cryptoBuild(
				type = SecurityHelper.AES
			)?.encrypt(jsonStr)
		return RequestModel(
			data = dataEncrypt
		)
	}
	
	fun <T> handlerCryptoData(handlerData: HandlerData, value: RequestModel, apiStr: String) {
		value.data
			?.let { data ->
				securityHelper?.decrypt(data)?.apply {
					this.toObjectData<ResponseData<T>>()
						?.let {
							//Biên dịch dữ liệu thành công
							when (it.code.toInt()) {
								in (200..299) -> {
									//Api xử lý dữ liệu thành công
									//Xử lý loại dữ liệu kết quả trả về cho đúng cái cần
									handlerData.onDataSuccess(apiStr, it.data)
								}
								in (401..499) -> {
									//Api cần tạo lại token
									error.postValue(Pair(it.code.toInt(), it.message))
								}
								in (500..599) -> {
									//Server Api có lỗi
									error.postValue(Pair(it.code.toInt(), it.message))
								}
							}
						}
						?: kotlin.run {
							//Lỗi trình biên dịch
							error.postValue(
								Pair(
									1,
									"Quá trình biên dịch dữ liệu xảy ra lỗi, vui lòng thử lại sau."
								)
							)
						}
				} ?: kotlin.run {
					//Lỗi trình biên dịch dữ liệu
					error.postValue(
						Pair(
							1,
							"Quá trình biên dịch dữ liệu xảy ra lỗi, vui lòng thử lại sau."
						)
					)
				}
			} ?: kotlin.run {
			//Lỗi API
			error.postValue(
				Pair(
					1,
					"Quá trình truy vấn dữ liệu xảy ra lỗi, vui lòng thử lại sau."
				)
			)
		}
	}
	
	fun handlerNonCryptoData(handlerData: HandlerData, value: ResponseData<*>, apiStr: String) {
		value.apply {
			if(apiStr == Constants.API_FINISH){
				if(this.code == "1"){
					handlerData.onDataSuccess(apiStr, this)
				}
				if(this.code == "0"){
					error.postValue(Pair(0, ""))
				}
			}
			when (code.toInt()) {
				in (200..299) -> {
					//Api xử lý dữ liệu thành công
					//Xử lý loại dữ liệu kết quả trả về cho đúng cái cần
					handlerData.onDataSuccess(apiStr, data)
				}
				in (401..499) -> {
					//Api cần tạo lại token
					error.postValue(Pair(code.toInt(), message))
				}
				in (500..599) -> {
					//Server Api có lỗi
					error.postValue(Pair(code.toInt(), message))
				}
			}
		}
	}
	
	fun handlerError(throwable: Throwable) {
		when (throwable) {
			is IOException -> {
				error.postValue(
					Pair(
						2,
						"Kết nối tới Internet đã xảy ra lỗi, vui lòng thử lại sau."
					)
				)
			}
			else -> {
				throwable.getErrorBody()?.let { requestErrorBody ->
					if (requestErrorBody.code.isNullOrEmpty() || requestErrorBody.code?.isDigitsOnly() == false){
						error.postValue(
							Pair(
								1,
								"Đã có lỗi xảy ra vui lòng thử lại sau."
							)
						)
					} else {
						when(requestErrorBody.code?.toInt()){
							in 401 .. 499 -> {
								error.postValue(
									Pair(
										requestErrorBody.code?.toInt()!!,
										"Đã có lỗi xảy ra vui lòng thử lại sau."
									)
								)
							}
							in 500 .. 599 -> {
								error.postValue(
									Pair(
										requestErrorBody.code?.toInt()!!,
										requestErrorBody.message ?: "Lỗi hệ thống."
									)
								)
							}
							else ->{
								if (requestErrorBody.data == null) {
									error.postValue(
										Pair(
											1,
											"Quá trình biên dịch dữ liệu xảy ra lỗi, vui lòng thử lại sau."
										)
									)
								} else {
									SecurityHelper.instance()
										.cryptoBuild(type = SecurityHelper.AES)
										?.decrypt(requestErrorBody.data)
										?.toObjectData<ResponseData<ErrorModel>>()
										?.let { modelError ->
											error.postValue(
												Pair(
													10,
													modelError.message
												)
											)
											
										} ?: kotlin.run {
										error.postValue(
											Pair(
												1,
												"Quá trình biên dịch dữ liệu xảy ra lỗi, vui lòng thử lại sau."
											)
										)
									}
								}
							}
						}
					}
				} ?: kotlin.run {
					error.postValue(
						Pair(
							1,
							"Quá kết nối tới Internet xảy ra lỗi, vui lòng thử lại sau."
						)
					)
				}
			}
		}
	}
	
	fun <T> handlerSuccess(handlerData: HandlerData, value: Any, apiStr: String){
		when(value){
			is RequestModel -> handlerCryptoData<T>(handlerData, value, apiStr)
			is ResponseData<*> -> handlerNonCryptoData(handlerData, value, apiStr)
		}
	}
	abstract fun clear()
}

interface HandlerData {
	fun onDataSuccess(api: String, data: Any?)
}