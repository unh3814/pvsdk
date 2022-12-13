package com.pvcombank.sdk.repository

import ResponseData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pvcombank.sdk.util.NetworkUtil.getErrorBody
import com.pvcombank.sdk.util.Utils.toObjectData
import com.pvcombank.sdk.util.security.SecurityHelper
import java.io.IOException

abstract class MyBaseRepository {
	private val securityHelper = SecurityHelper.instance().cryptoBuild(type = SecurityHelper.AES)
	val error = MutableLiveData<Pair<String?, String?>>()
	val gson: Gson get() = GsonBuilder().create()

	private fun <T> handlerCryptoData(value: String?, callBack: MutableLiveData<T>) {
		if (value?.isEmpty() == true) {
			error.postValue(Pair("2", null))
			return
		}
		securityHelper?.decrypt(value)?.apply {
			this.toObjectData<ResponseData<T>>()?.let {
				//Biên dịch dữ liệu thành công
				if ((it.code ?: "0").toInt() in 200..299 && it.data != null) {
					callBack.postValue(it.data)
				} else {
					error.postValue(Pair(it.code, it.message))
				}
			} ?: kotlin.run {
				//Lỗi trình biên dịch
				error.postValue(Pair("1", null))
			}
		} ?: kotlin.run {
			//Lỗi trình biên dịch dữ liệu
			error.postValue(
				Pair("1", null)
			)
		}
	}

	private fun <T> handlerNonCryptoData(
		value: ResponseData<*>,
		callBack: MutableLiveData<T>
	) {
		value.apply {
			if ((code ?: "0").toInt() in 200..299 && data != null) {
				callBack.postValue(data as T)
			} else {
				error.postValue(Pair(code, message))
			}
		}
	}

	fun handlerError(throwable: Throwable) {
		when (throwable) {
			is IOException -> {
				error.postValue(
					Pair("2", "Kết nối tới Internet đã xảy ra lỗi, vui lòng thử lại sau.")
				)
			}
			else -> {
				throwable.getErrorBody()?.let { requestErrorBody ->
					if (requestErrorBody.data is String) {
						securityHelper?.decrypt(requestErrorBody.data as String)
							?.toObjectData<ResponseData<Any>>()
							?.apply {
								error.postValue(
									Pair(
										"${code ?: 1}",
										message
									)
								)
							} ?: kotlin.run {
							error.postValue(Pair("1", null))
						}
					} else {
						error.postValue(
							Pair(
								"${requestErrorBody.code ?: 1}",
								requestErrorBody.message
							)
						)
					}
				} ?: kotlin.run {
					error.postValue(Pair("1", null))
				}
			}
		}
	}

	fun <T> handlerSuccess(value: ResponseData<*>, callBack: MutableLiveData<T>) {
		if (value.data is String) {
			handlerCryptoData(value.data as? String, callBack)
		} else {
			handlerNonCryptoData(value, callBack)
		}
	}

	abstract fun clear()
}