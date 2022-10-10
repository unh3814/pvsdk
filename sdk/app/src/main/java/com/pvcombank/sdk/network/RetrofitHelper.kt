package com.pvcombank.sdk.network

import com.pvcombank.sdk.model.Constants
import com.pvcombank.sdk.model.MasterModel
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
		val client = OkHttpClient.Builder()
			.readTimeout(60, TimeUnit.SECONDS)
			.connectTimeout(60, TimeUnit.SECONDS)
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
					.addHeader("signature", MasterModel.getInstance().uniId)
					.build()
				chain.proceed(request)
			})
			.addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) })
			.addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.HEADERS) })
			.build()
		return Retrofit.Builder()
			.baseUrl(baseURL)
			.addCallAdapterFactory(RxJava3CallAdapterFactory.create())
			.addConverterFactory(GsonConverterFactory.create())
			.client(client)
			.build()
	}
	fun createNewServices(baseURL: String): Retrofit {
		val client = OkHttpClient.Builder()
			.readTimeout(60, TimeUnit.SECONDS)
			.connectTimeout(60, TimeUnit.SECONDS)
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
					.addHeader("signature", MasterModel.getInstance().uniId)
					.build()
				chain.proceed(request)
			})
			.addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) })
			.addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.HEADERS) })
			.build()
		return Retrofit.Builder()
			.baseUrl(baseURL)
			.addCallAdapterFactory(ApiResponseCallFactory())
			.addConverterFactory(GsonConverterFactory.create())
			.client(client)
			.build()
	}
}