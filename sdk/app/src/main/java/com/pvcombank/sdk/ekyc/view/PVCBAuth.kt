package com.pvcombank.sdk.ekyc.view

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.pvcombank.sdk.ekyc.PVCBAuthListener
import com.pvcombank.sdk.ekyc.model.Constants
import com.pvcombank.sdk.ekyc.model.MasterModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject

class PVCBAuth() {
    //view
    private var listener: PVCBAuthListener? = null
    private var activity: FragmentActivity? = null
    private var isProduction: Boolean = false
    fun build(
        activity: FragmentActivity,
        isProduction: Boolean,
        userIdPartner: String? = null,
        appCode: String,
        listener: PVCBAuthListener
    ) {
        this.activity = activity
        this.listener = listener
        this.isProduction = isProduction
		Constants.ID_PARTNER = userIdPartner
        Constants.APP_CODE = appCode
    }

    fun startRegister() {
        with(MasterModel.getInstance()){
            isProduction = this@PVCBAuth.isProduction
            errorString = PublishSubject.create()
            errorString
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe {
                    listener?.onError(it)
                }
            successString = PublishSubject.create()
            successString
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe {
                    listener?.onSuccess(it)
                }
        }
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