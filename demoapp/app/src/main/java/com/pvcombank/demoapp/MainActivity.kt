package com.pvcombank.demoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
	}
	
	override fun onStart() {
		super.onStart()
		
		com.pvcombank.sdk.ekyc.view.PVCBAuth().apply {
			build(this@MainActivity, object : com.pvcombank.sdk.ekyc.PVCBAuthListener{
				override fun onError(message: String) {
				}
				
				override fun onSuccess(message: String) {
				}
			})
			startRegister()
		}
	}
}