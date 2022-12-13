package com.pvcombank.sdk.model

object Constants {
	//region Cryptography
	const val secretKey = "9875cce6826dbc1fc9083c12c6d75642"
	const val iv = "053D0C386EE38077"
	const val algorithm = "AES"
	const val algorithm_generate = "PBKDF2WithHmacSHA256"
	const val transformation = "AES/CBC/PKCS7Padding"
	const val provider = "BC"
	const val file_name = "PCV@beh5pkj!ufx6dky"
	const val rpl_1 = "+"
	const val rpl_2 = "%2b"
	//endregion Cryptography

	var TOKEN: String = ""
	var TOKEN_REFRESH = ""

	const val HTTP_TIME_OUT = 60L
}