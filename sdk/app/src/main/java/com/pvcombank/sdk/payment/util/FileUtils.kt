package com.pvcombank.sdk.payment.util

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object FileUtils {
	const val cardFileName = "card_image.jpg"
	const val cardBackFileName = "card_back_image.jpg"
	const val faceFileName = "face_image.jpg"
	const val faceFileNameCloseEyes = "face_image_close_eyes.jpg"
	const val faceFileNameRight = "face_image_right.jpg"
	const val faceFileNameLeft = "face_image_left.jpg"
	
	fun saveFile(context: Context, bitmap: Bitmap, fileName: String?) {
		if (fileName == null || fileName.isEmpty()) return
		val savedPhoto = File(context.cacheDir, fileName)
		try {
			val outputStream = FileOutputStream(savedPhoto.path)
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
			//outputStream.write(capturedImage);
			outputStream.flush()
			outputStream.close()
		} catch (e: IOException) {
			e.printStackTrace()
		}
	}
	
	fun getCardFile(context: Context): File {
		return File(context.cacheDir, cardFileName)
	}
	
	fun getFaceFile(context: Context): File {
		return File(context.cacheDir, faceFileName)
	}
	
	fun getFaceFileCloseEyes(context: Context): File {
		return File(context.cacheDir, faceFileNameCloseEyes)
	}
	
	fun getFaceFileRight(context: Context): File {
		return File(context.cacheDir, faceFileNameRight)
	}
	
	fun getFaceFileLeft(context: Context): File {
		return File(context.cacheDir, faceFileNameLeft)
	}
	
	fun getCardBackFile(context: Context): File {
		return File(context.cacheDir, cardBackFileName)
	}
}