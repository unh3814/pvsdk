package com.pvcombank.sdk.ekyc.util.security

import android.content.Context
import android.util.Base64
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.security.interfaces.RSAPrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

class RsaCrypto() : PVCryptography {
	private lateinit var context: Context
	private lateinit var publicKey: PublicKey
	private lateinit var privateKey: PrivateKey
	
	constructor(context: Context) : this() {
		this.context = context
	}
	
	override fun encrypt(str: String): String {
		try {
			val cipher = Cipher.getInstance(ALGORITHM_CIPHER)
			
			return ""
		} catch (e: Exception){
			return ""
		}
	}
	
	override fun decrypt(str: String?): String {
		try {
			return ""
		} catch (e: Exception){
			return ""
		}
	}
	
	fun getPrivateKey(data: String): RSAPrivateKey? {
		val tPrivateKey = data.replace("-----BEGIN RSA PRIVATE KEY-----", "")
			.replace("-----END RSA PRIVATE KEY-----", "")
			.replace("\n", "")
			.replace(" ", "")
		val byteArray = Base64.decode(tPrivateKey, Base64.NO_WRAP)
		val spec = PKCS8EncodedKeySpec(byteArray)
		val keyFactory = KeyFactory.getInstance(ALGORITHM)
		return (keyFactory.generatePrivate(spec) as? RSAPrivateKey)
	}
	
	fun getPublicKey(data: String): PublicKey? {
		val tPrivateKey = data.replace("-----BEGIN PUBLIC KEY-----", "")
			.replace("-----END PUBLIC KEY-----", "")
			.replace("\n", "")
			.replace(" ", "")
		val byteArray = Base64.decode(tPrivateKey, Base64.NO_WRAP)
		val spec = X509EncodedKeySpec(byteArray)
		val keyFactory = KeyFactory.getInstance(ALGORITHM)
		return keyFactory.generatePublic(spec)
	}
	
	fun verifySignature(
		msg: ByteArray,
		signature: ByteArray
	): Boolean {
		val s = Signature.getInstance(ALGORITHM_SIGNATURE).apply {
			initVerify(publicKey)
			update(msg)
		}
		return s.verify(signature)
	}
	
	fun getSignature(data: ByteArray): String {
		return Base64.encodeToString(
			Signature.getInstance(ALGORITHM_SIGNATURE)
				.apply {
					initSign(privateKey)
					update(data)
				}.sign(),
			Base64.NO_WRAP
		)
	}
	
	companion object {
		private const val ALGORITHM = "RSA"
		private const val ALGORITHM_SIGNATURE = "SHA256withRSA"
		private const val ALGORITHM_CIPHER = "RSA/ECB/PKCS1Padding"
		private var INSTANCE: RsaCrypto? = null
		fun getOrCreate(context: Context): RsaCrypto {
			return INSTANCE ?: synchronized(this) {
				INSTANCE ?: RsaCrypto(context).also {
					INSTANCE = it
				}
			}
		}
	}
}