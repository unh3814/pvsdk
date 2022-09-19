package com.pvcombank.sdk.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager

object PermissionUtil {
	const val PERMISSION_CALL_REQUEST = 10002
	private val permissionCall = arrayOf(
		Manifest.permission.CALL_PHONE
	)
	
	fun isCallHadPermission(context: Context): Boolean {
		return permissionCall.all {
			context.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
		}
	}
	
	fun requestCallPermission(activity: Activity){
		activity.requestPermissions(permissionCall, PERMISSION_CALL_REQUEST)
	}
}