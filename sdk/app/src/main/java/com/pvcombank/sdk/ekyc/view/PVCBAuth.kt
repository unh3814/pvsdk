package com.pvcombank.sdk.ekyc.view

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.pvcombank.sdk.ekyc.PVCBAuthListener
import com.pvcombank.sdk.ekyc.model.Constants
import com.pvcombank.sdk.ekyc.model.MasterModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class PVCBAuth() {
    //view
    private var listener: PVCBAuthListener? = null

    private val masterModel = MasterModel.getInstance()
    private var activity: FragmentActivity? = null

    fun build(
        activity: FragmentActivity,
        isProduction: Boolean,
        userIdPartner: String? = null,
        listener: PVCBAuthListener
    ) {
        this.activity = activity
        this.listener = listener
		Constants.ID_PARTNER = userIdPartner
        if (isProduction) {
            Constants.ONBOARDING_URL = "https://onboarding-api.pvcombank.com.vn/api/"
            Constants.CHECK_ACC_URL = "https://mbanking255.pvcombank.com.vn/api/"
            Constants.BASE_URL_OTP = "https://awsapi.pvcombank.com.vn/v1/onboarding/"
        }

        masterModel.errorString
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe {
                listener?.onError(it)
            }
        masterModel.successString
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe {
                listener?.onSuccess(it)
            }
    }

    fun startRegister() {
        activity?.apply {
            startActivity(
                Intent(
                    this,
                    RegisterActivity::class.java
                )
            )
        }
    }
}