package com.pvcombank.sdk.payment.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesSafe(private val context: Context) {
	private val Context.dataStore by preferencesDataStore(name = "secret_data")
	
	companion object {
		private var INSTANCE: PreferencesSafe? = null
		fun getInstance(context: Context): PreferencesSafe {
			return INSTANCE ?: PreferencesSafe(context).apply {
				INSTANCE = this
			}
		}
	}
	
	private object PreferencesKey {
		//RSA
		val PUBLIC_KEY = stringPreferencesKey("secret.public")
		val PRIVATE_KEY = stringPreferencesKey("secret.private")
		//AES
		val SECRET_KEY = stringPreferencesKey("secret.key")
		val IV = stringPreferencesKey("secret.iv")
		val SALT = stringPreferencesKey("secret.salt")
	}
	
	suspend fun setPublicKey(data: String) {
		context.dataStore.edit {
			it[PreferencesKey.PUBLIC_KEY] = data
		}
	}
	
	fun getPublicKey(): Flow<String?> {
		return context.dataStore.data.map {
			it[PreferencesKey.PUBLIC_KEY]
		}
	}
	
	suspend fun setPrivateKey(data: String) {
		context.dataStore.edit {
			it[PreferencesKey.PRIVATE_KEY] = data
		}
	}
	
	fun getPrivateKey(): Flow<String?> {
		return context.dataStore.data.map {
			it[PreferencesKey.PRIVATE_KEY]
		}
	}
	
	
}