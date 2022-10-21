package com.pvcombank.sdk.ekyc.util

import android.content.Context
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date

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
	
	fun Bitmap.toFile(context: Context): File {
		val cacheFile = File(context.cacheDir, "cachePVCB${Date().time}.jpg")
		cacheFile.createNewFile()
		val bos = ByteArrayOutputStream()
		this.compress(Bitmap.CompressFormat.JPEG, 100, bos)
		val bitmapData = bos.toByteArray()
		val fos = FileOutputStream(cacheFile)
		fos.write(bitmapData)
		fos.flush()
		fos.close()
		return cacheFile
	}
}