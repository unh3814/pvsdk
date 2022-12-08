package com.pvcombank.demoapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.pvcombank.sdk.payment.PVCBAuthListener
import com.pvcombank.sdk.payment.view.PVCBAuth

class MainActivity : AppCompatActivity() {
	private var textThanhToan: TextView? = null
	private var btnThanhToan: Button? = null
	private var edtCurrency: EditText? = null
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		btnThanhToan = findViewById(R.id.btn_thanh_toan)
		textThanhToan = findViewById(R.id.tv_thanh_toan)
		edtCurrency = findViewById(R.id.edt_currency)
		textThanhToan?.text = "Mã đơn hàng: #005002\nNội dung: Thanh toan vien phi tai BV"
		btnThanhToan?.setOnClickListener {
			startPayment()

		}
	}
	
	fun startPayment() {
		PVCBAuth().apply {
			hideKeyboard()
			setClient(
				clientId = "vietsens-sdk",
				clientSecret = "97392180-9aeb-4fe4-9c24-0676d35b4505",
				currency = edtCurrency?.text?.toString() ?: "123456789",
				idOrder = "005002",
				appUnitId = "ONELINK"
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