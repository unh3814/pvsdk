package com.pvcombank.sdk.ekyc.util.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences

class SharePreferenceSafety() {
	private lateinit var masterKeys: String
	private lateinit var sharePreferences: SharedPreferences
	private val editor get() = sharePreferences.edit()
	constructor(masterKeys: String, context: Context) : this() {
		this.masterKeys = masterKeys
		this.sharePreferences = EncryptedSharedPreferences.create(
			"secret_shared_prefs",
			masterKeys,
			context,
			EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
			EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
		)
	}
	
	companion object {
		private var INSTANCE: SharePreferenceSafety? = null
		fun getOrCreate(context: Context, masterKeys: String): SharePreferenceSafety {
			return INSTANCE ?: synchronized(this) {
				INSTANCE ?: SharePreferenceSafety(masterKeys, context).also {
					INSTANCE = it
				}
			}
		}
	}
}