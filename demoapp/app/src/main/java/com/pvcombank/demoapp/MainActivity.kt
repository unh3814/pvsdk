package com.pvcombank.demoapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.pvcombank.sdk.ekyc.PVCBAuthListener
import com.pvcombank.sdk.ekyc.view.PVCBAuth

class MainActivity : AppCompatActivity(), PVCBAuthListener {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
	}
	
	override fun onStart() {
		super.onStart()
		PVCBAuth().apply {
			build(
				this@MainActivity,
				true,
				null,
				this@MainActivity
			)
			startRegister()
		}
	}

	override fun onError(message: String) {
		Log.e("ERROR", message)
	}

	override fun onSuccess(message: String) {
		Log.d("SUCCESS", message)
	}

}