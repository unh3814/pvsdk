package com.pvcombank.sdk.util.security

import android.util.Base64
import com.pvcombank.sdk.model.Constants
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class AesCrypto : PVCryptography {
	private val cipher = Cipher.getInstance(Constants.transformation, Constants.provider)
	
	override fun encrypt(str: String): String {
		val secretKeySpec = SecretKeySpec(Constants.secretKey.toByteArray(Charsets.UTF_8), Constants.algorithm)
		val ivParameterSpec = IvParameterSpec(Constants.iv.toByteArray())
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)
		val cipherText = cipher.doFinal(str.toByteArray())
		val strBase64 = Base64.encodeToString(cipherText, Base64.NO_WRAP)
		return strBase64.replace(Constants.rpl_1, Constants.rpl_2)
	}
	
	override fun decrypt(str: String?): String {
		if (str == null) return ""
		val secretKeySpec = SecretKeySpec(
			Constants.secretKey.toByteArray(Charsets.UTF_8),
			Constants.algorithm
		)
		val ivParameterSpec = IvParameterSpec(cipher.iv)
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)
		val stringToDecrypt = str.replace(Constants.rpl_2, Constants.rpl_1)
		val byteToDecrypt = Base64.decode(stringToDecrypt, Base64.NO_WRAP)
		cipher.doFinal(byteToDecrypt).apply {
			return String(this, Charsets.UTF_8)
		}
	}
	
	fun genKey(password: CharArray, salt: ByteArray): SecretKeySpec {
		val factory = SecretKeyFactory.getInstance(Constants.algorithm_generate)
		val spec = PBEKeySpec(password, salt, 10000, 32)
		val tmp = factory.generateSecret(spec)
		return SecretKeySpec(tmp.encoded, Constants.algorithm)
	}
	
	companion object {
		private var INSTANCE: AesCrypto? = null
		fun getOrCreate(): AesCrypto {
			return INSTANCE ?: synchronized(this) {
				INSTANCE ?: AesCrypto().also {
					INSTANCE = it
				}
			}
		}
	}
}