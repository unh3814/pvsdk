package com.pvcombank.sdk.util.security

import android.content.Context
import androidx.security.crypto.MasterKeys

class SecurityHelper {
	companion object {
		const val AES = 1
		const val RSA = AES + 1
		const val SHARE_PREFERENCES = RSA + 1
		const val FILE = SHARE_PREFERENCES + 1
		
		private var INSTANCE: SecurityHelper? = null
		
		fun instance(): SecurityHelper {
			return INSTANCE ?: synchronized(this) {
				INSTANCE ?: SecurityHelper().also {
					INSTANCE = it
				}
			}
		}
	}
	
	private var aesCrypt: AesCrypto? = null
	private var rsaCrypt: RsaCrypto? = null
	private val masterKey get() = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
	
	fun cryptoBuild(context: Context? = null, type: Int): PVCryptography? {
		return when (type) {
			AES -> {
				aesCrypt = AesCrypto.getOrCreate()
				aesCrypt
			}
			RSA -> {
				if (context == null) return null
				rsaCrypt = RsaCrypto.getOrCreate(context)
				rsaCrypt
			}
			FILE -> {
				if (context == null) return null
				FileCrypto.getOrCreate(context, masterKey)
			}
			else -> null
		}
	}
	
	fun sharePreferenceBuild(context: Context): SharePreferenceSafety {
		return SharePreferenceSafety.getOrCreate(context, masterKey)
	}
}