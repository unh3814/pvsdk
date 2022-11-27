package com.pvcombank.sdk.ekyc.network

import com.pvcombank.sdk.ekyc.BuildConfig
import com.pvcombank.sdk.ekyc.model.Constants
import com.pvcombank.sdk.ekyc.model.MasterModel
import com.pvcombank.sdk.ekyc.util.Utils
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

class RetrofitHelper {
	companion object {
		private var INSTANCE: RetrofitHelper? = null
		fun instance(): RetrofitHelper {
			return INSTANCE ?: RetrofitHelper().apply {
				INSTANCE = this
			}
		}
	}
	
	fun createServices(baseURL: String): Retrofit {
		val url = if (MasterModel.getInstance().isProduction){
			when(baseURL){
				Constants.ONBOARDING_URL -> Constants.ONBOARDING_URL_PRODUCT
				Constants.CHECK_ACC_URL -> Constants.CHECK_ACC_URL_PRODUCT
				Constants.BASE_URL_OTP -> Constants.BASE_URL_OTP_PRODUCT
				else -> baseURL
			}
		} else baseURL
		val client = OkHttpClient.Builder()
			.readTimeout(60, TimeUnit.SECONDS)
			.connectTimeout(90, TimeUnit.SECONDS)
			.callTimeout(60, TimeUnit.SECONDS)
			.writeTimeout(60, TimeUnit.SECONDS)
			.addInterceptor(Interceptor { chain ->
				val request = chain.request().newBuilder()
					.addHeader("Authorization", Constants.TOKEN)
					.addHeader(
						"x-idempotency-key",
						UUID.randomUUID().toString().lowercase(Locale.ROOT)
					)
					.addHeader("Content-Type", "application/json")
					.addHeader("app_unit_id", MasterModel.getInstance().appUnitID ?: "")
					.addHeader("app_id", Constants.VIETSENS_ID)
					.addHeader("signature", Utils.randomUniID())
					.build()
				chain.proceed(request)
			})
			.addInterceptor(HttpLoggingInterceptor().apply {
				if (BuildConfig.DEBUG){
					level = HttpLoggingInterceptor.Level.BODY
				}
			})
			.addInterceptor(HttpLoggingInterceptor().apply {
				if (BuildConfig.DEBUG){
					level = HttpLoggingInterceptor.Level.HEADERS
				}
			})
			.build()
		return Retrofit.Builder()
			.baseUrl(url)
			.addCallAdapterFactory(RxJava3CallAdapterFactory.create())
			.addConverterFactory(GsonConverterFactory.create())
			.client(client)
			.build()
	}
}