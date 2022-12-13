package com.pvcombank.sdk.network

import ResponseData
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface API {
    @GET("/api")
    fun getAPI(): Observable<ResponseData<Any>>

    @POST("/api")
    fun postAPI(): Observable<ResponseData<Any>>

    @PUT("/api")
    fun putAPI(): Observable<ResponseData<Any>>

}