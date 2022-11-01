package com.pvcombank.sdk.payment.util

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pvcombank.sdk.R
import java.text.NumberFormat
import java.util.*

object Utils {
	fun String.formatStringCurrency(): String {
		val locale = Locale("vi", "vn")
		return NumberFormat.getInstance(locale).format(this.toDouble())
	}
	
	fun FragmentActivity.openFragment(
		clazz: Class<out Fragment>,
		arg: Bundle,
		addBackStack: Boolean
	) {
		supportFragmentManager.beginTransaction()
			.replace(R.id.hosts_fragment, clazz, arg)
			.addToBackStack(clazz.simpleName)
			.setReorderingAllowed(addBackStack)
			.commit()
	}
	
	inline fun <reified T> String.toObjectData(): T? {
		val type = object : TypeToken<T>() {}.type
		var result: T? = null
		try{
			result = Gson().fromJson<T>(this, type)
		} catch (e: Exception){
		}
		 return result
	}
}