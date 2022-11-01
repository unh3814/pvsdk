package com.pvcombank.sdk.payment.util.security

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.security.crypto.EncryptedFile
import com.pvcombank.sdk.payment.model.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream
import java.io.File

class FileCrypto() : PVCryptography {
	private lateinit var masterKeys: String
	private lateinit var context: Context
	
	constructor(context: Context, masterKeys: String) : this() {
		this.context = context
		this.masterKeys = masterKeys
	}
	
	override fun encrypt(str: String): String {
		try {
			EncryptedFile
				.Builder(
					getOrCreateFile(),
					context.applicationContext,
					masterKeys,
					EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
				)
				.build()
				.also {
					it.openFileOutput().apply {
						write(str.toByteArray(Charsets.UTF_8))
						flush()
						close()
					}
				}
		} catch (e: Exception) {
			Log.e("ERROR", e.message.toString())
		}
		return ""
	}
	
	override fun decrypt(str: String?): String {
		try {
			EncryptedFile
				.Builder(
					getOrCreateFile(),
					context.applicationContext,
					masterKeys,
					EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
				)
				.build()
				.also {
					val inputStream = it.openFileInput()
					val resultByteArray = ByteArrayOutputStream()
					var nextByte = inputStream.read()
					while (nextByte != -1) {
						resultByteArray.write(nextByte)
						nextByte = inputStream.read()
					}
					inputStream.close()
					return Base64.encodeToString(resultByteArray.toByteArray(), Base64.DEFAULT)
				}
		} catch (e: Exception) {
			Log.e("ERROR", e.message.toString())
		}
		return ""
	}
	
	private fun getOrCreateFile(): File {
		if (!File(context.cacheDir, Constants.file_name).exists()) {
			return File.createTempFile(Constants.file_name, "txt")
		}
		return File(context.cacheDir, Constants.file_name)
	}
	
	fun clearCacheFile() {
		runBlocking(Dispatchers.IO) {
			context.deleteFile(Constants.file_name)
		}
	}
	
	companion object {
		private var INSTANCE: FileCrypto? = null
		fun getOrCreate(context: Context, masterKeys: String): FileCrypto {
			return INSTANCE ?: synchronized(this) {
				INSTANCE ?: FileCrypto(context, masterKeys).also {
					INSTANCE = it
				}
			}
		}
	}
}