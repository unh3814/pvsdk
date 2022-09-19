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
		activity: FragmentActivity
	) {
		this.activity = activity
	}
	
	fun setClient(
		clientId: String,
		clientSecret: String,
		currency: String,
		idOrder: String,
		appUnitId: String
	) {
		masterModel.apply {
			this.clientId = clientId
			this.clientSecret = clientSecret
			this.orderCurrency = currency
			this.idOrder = idOrder
			this.appUnitID = appUnitId
		}
		masterModel.errorString
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeOn(Schedulers.io())
			.subscribe{
				listener?.onError(it)
			}
		masterModel.successString
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeOn(Schedulers.io())
			.subscribe{
				listener?.onSuccess(it)
			}
	}
	
	fun setListener(listener: PVCBAuthListener) {
		this.listener = listener
	}
	
	fun show() {
		activity?.apply {
			startActivity(
				Intent(this, PVCBActivity::class.java)
			)
		}
	}
}