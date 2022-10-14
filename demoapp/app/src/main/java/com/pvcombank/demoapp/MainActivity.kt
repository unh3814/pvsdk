package com.pvcombank.demoapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.pvcombank.sdk.PVCBAuthListener
import com.pvcombank.sdk.view.PVCBAuth

class MainActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
	}
	
	override fun onStart() {
		super.onStart()
		PVCBAuth().apply {
			build(this@MainActivity, object : PVCBAuthListener{
				override fun onError(message: String) {
					Log.d("ERROR", "")
				}
				
				override fun onSuccess(message: String) {
					Log.d("SUCCESS", "")
				}
			})
			startRegister()
		}
	}
}