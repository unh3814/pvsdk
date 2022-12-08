package com.pvcombank.sdk.payment.model

import com.pvcombank.sdk.payment.util.security.SecurityHelper
import com.google.gson.Gson
import com.pvcombank.sdk.BuildConfig
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.*

class MasterModel {
	var clientId: String? = null
	var clientSecret: String? = null
	var orderCurrency: String? = null
	var idOrder: String? = null
	var orderDesc: String? = null
	var appUnitID: String? = null
	var uuidOfOTP: String? = null
	var timeLogin: Long = 0
	var uniId: String = ""
		get() {
			val obj = Gson().toJson(
				hashMapOf(
					"app_id" to BuildConfig.LIBRARY_PACKAGE_NAME,
					"time_stamp" to Date().time.toString()
				)
			)
			return SecurityHelper.instance()
				.cryptoBuild(type = SecurityHelper.AES)
				?.encrypt(obj) ?: ""
		}
	val ocrApiKey = "KFxtLjFTavCDmNFE7dqZiCowhyM02ZWO"
	val errorString: PublishSubject<String> = PublishSubject.create()
	val successString: PublishSubject<String> = PublishSubject.create()

	companion object {
		@JvmStatic
		private var INSTANCE: MasterModel? = null
		
		fun getInstance(): MasterModel {
			return INSTANCE ?: MasterModel().apply {
				INSTANCE = this
			}
		}
	}
}