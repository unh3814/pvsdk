package com.pvcombank.sdk.network

import com.pvcombank.sdk.ekyc.BuildConfig
import com.pvcombank.sdk.model.Constants
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
	
	fun createServices(url: String): Retrofit {
		val client = OkHttpClient.Builder()
			.readTimeout(Constants.HTTP_TIME_OUT, TimeUnit.SECONDS)
			.connectTimeout(Constants.HTTP_TIME_OUT, TimeUnit.SECONDS)
			.callTimeout(Constants.HTTP_TIME_OUT, TimeUnit.SECONDS)
			.writeTimeout(Constants.HTTP_TIME_OUT, TimeUnit.SECONDS)
			.addInterceptor(Interceptor { chain ->
				val request = chain.request().newBuilder()
					.addHeader("Authorization", Constants.TOKEN)
					.addHeader(
						"x-idempotency-key",
						UUID.randomUUID().toString().lowercase(Locale.ROOT)
					)
					.addHeader("Content-Type", "application/json")
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