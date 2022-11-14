package com.pvcombank.sdk.ekyc.repository

import androidx.lifecycle.MutableLiveData
import com.pvcombank.sdk.ekyc.model.Constants
import com.pvcombank.sdk.ekyc.model.ResponseData
import com.pvcombank.sdk.ekyc.model.request.CheckAccountRequest
import com.pvcombank.sdk.ekyc.model.request.RequestFinish
import com.pvcombank.sdk.ekyc.model.request.RequestUpdatePassword
import com.pvcombank.sdk.ekyc.model.request.RequestVerifySelfies
import com.pvcombank.sdk.ekyc.network.ApiEKYC
import com.pvcombank.sdk.ekyc.network.RetrofitHelper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.RequestBody

class OnBoardingRepository : PVRepository(), HandlerData {
    private val apiServices: ApiEKYC = RetrofitHelper.instance()
        .createServices(Constants.ONBOARDING_URL)
        .create(ApiEKYC::class.java)
    private val retrofitUpdatePassword = RetrofitHelper.instance()
        .createServices(Constants.BASE_URL_OTHER)
        .create(ApiEKYC::class.java)
    private val retrofitCheckAccount = RetrofitHelper.instance()
        .createServices(Constants.CHECK_ACC_URL)
        .create(ApiEKYC::class.java)
    val observerUpdatePassword = MutableLiveData<String>()
    val observerFinish = MutableLiveData<ResponseData<*>>()
    fun verifyCard(
        requestBody: RequestBody,
        callBack: (HashMap<String, Any>) -> Unit
    ) {
        val result = hashMapOf<String, Any>()
        apiServices.verifyCard(requestBody)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    result["success"] = it
                    callBack.invoke(result)
                },
                { handlerError(it) }
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
                { handlerError(it) }
            )
    }

    fun finish(requestFinish: RequestFinish) {
        apiServices.finish(requestFinish)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                { handlerSuccess<Any>(this, it, Constants.API_FINISH) },
                { handlerError(it) }
            )
    }

    fun checkAccount(request: CheckAccountRequest, callBack: (HashMap<String, Any>) -> Unit) {
        val result = hashMapOf<String, Any>()
        retrofitCheckAccount.checkAccount(request)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    if (it.C == "001") {
                        if (it.E?.status == "1") {
                            //Dừng lại
                            result["stop"] = it.E.message ?: ""
                        }
                        if (it.E?.status == "2") {
                            //Đi tiếp
                            result["next"] =
                                "Quý khách đã có Tài khoản và dịch vụ NHĐT tại PVcomBank, vui lòng đăng nhập PV-Mobile banking để trải nghiệm dịch vụ. Chi tiết liên hệ 1900 5555 92"
                        }
                    } else if (it.C == "999") {
                        result["error_network"] = "Gọi thất bại"
                    } else {
                        result["fail"] = it.E?.message ?: ""
                    }
                    callBack.invoke(result)
                },
                { handlerError(it) }
            )
    }

    fun updatePassword(password: String) {
        val value = encryptRequest(RequestUpdatePassword(password))
        retrofitUpdatePassword.updatePassword(
            value
        ).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                { handlerSuccess<Any>(this, it, Constants.API_UPDATE_PASSWORD) },
                { handlerError(it) }
            )
    }

    override fun clear() {
        error.postValue(null)
        observerFinish.postValue(null)
        observerUpdatePassword.postValue(null)
    }

    override fun onDataSuccess(api: String, data: Any?) {
        when (api) {
            Constants.API_UPDATE_PASSWORD -> {
                observerUpdatePassword.postValue("success")
            }
            Constants.API_FINISH -> {
                observerFinish.postValue(data as? ResponseData<*>)
            }
        }
    }
}