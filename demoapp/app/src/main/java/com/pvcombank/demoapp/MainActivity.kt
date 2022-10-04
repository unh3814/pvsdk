package com.pvcombank.demoapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.pvcombank.sdk.PVCBAuthListener
import com.pvcombank.sdk.view.PVCBAuth

class MainActivity : AppCompatActivity() {
	private var btnThanhToan: Button? = null
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		btnThanhToan = findViewById(R.id.btn_thanh_toan)
		btnThanhToan?.setOnClickListener {
			startPayment()
		}

		findViewById<Button>(R.id.btn_register).setOnClickListener {
			PVCBAuth().apply {
				setClient(
					clientId = "vietsens-sdk",
					clientSecret = "97392180-9aeb-4fe4-9c24-0676d35b4505",
					currency = "90000",
					idOrder = "005002",
					appUnitId = "GEBIuX+mVEJzPZG/QuVkVQ=="
				)
				build(this@MainActivity)
				startRegister()
			}
		}
	}
	
	fun startPayment() {
		PVCBAuth().apply {
			hideKeyboard()
			setClient(
				clientId = "vietsens-sdk",
				clientSecret = "97392180-9aeb-4fe4-9c24-0676d35b4505",
				currency = "90000",
				idOrder = "005002",
				appUnitId = "GEBIuX+mVEJzPZG/QuVkVQ=="
			)
			setListener(object : PVCBAuthListener {
				override fun onError(message: String) {
					Log.d("ERROR", message)
				}
				
				override fun onSuccess(message: String) {
					Log.d("SUCCESS", message)
				}
			})
			build(this@MainActivity)
			show()
		}
	}
	
	fun hideKeyboard() {
		this.currentFocus?.let {
			(getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.apply {
				hideSoftInputFromWindow(it.windowToken, 0)
			}
		}
	}
}