package com.pvcombank.sdk.view

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.pvcombank.sdk.PVCBAuthListener
import com.pvcombank.sdk.model.MasterModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class PVCBAuth() {
	//view
	private var listener: PVCBAuthListener? = null
	
	private val masterModel = MasterModel.getInstance()
	private var activity: FragmentActivity? = null
	
	fun build(
		activity: FragmentActivity,
		listener: PVCBAuthListener
	) {
		this.activity = activity
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
		this.listener = listener
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