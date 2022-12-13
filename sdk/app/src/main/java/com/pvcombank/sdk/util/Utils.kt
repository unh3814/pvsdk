package com.pvcombank.sdk.util

import android.content.Context
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pvcombank.sdk.base.PVActivity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object Utils {
	fun String.formatStringCurrency(): String {
		val locale = Locale("vi", "vn")
		return NumberFormat.getInstance(locale).format(this.toDouble())
	}
	
	fun FragmentActivity.openFragment(
		clazz: Class<out Fragment>,
		arg: Bundle,
		addBackStack: Boolean? = true
	) {
		val hostFragmentID = (this as PVActivity<*>).fragmentHostID
		supportFragmentManager.beginTransaction()
			.replace(hostFragmentID, clazz, arg)
			.addToBackStack(clazz.simpleName)
			.setReorderingAllowed(addBackStack!!)
			.commit()
	}
	
	inline fun <reified T> String.toObjectData(): T? {
		val type = object : TypeToken<T>() {}.type
		var result: T? = null
		try {
			result = Gson().fromJson<T>(this, type)
		} catch (e: Exception) {
		}
		return result
	}

	fun String.dateConvert(from: String? = "dd/MM/yyyy", to: String): String{
		val dateFrom = SimpleDateFormat(from)
		val dateTo = SimpleDateFormat(to)
		return dateTo.format(dateFrom.parse(this))
	}
	
	fun onDrawableClick(rawView: TextView, position: Int, callback: () -> Unit) {
		rawView.setOnTouchListener { _, event ->
			onEventDrawableClick(rawView, event, position, callback)
		}
	}
	
	private fun onEventDrawableClick(
		view: TextView,
		event: MotionEvent,
		position: Int,
		callback: () -> Unit
	): Boolean {
		val location = (view.right - view.compoundDrawables[position].bounds.width())
		if (event.rawX >= location) {
			callback.invoke()
			return true
		}
		return false
	}
	
	fun TextView.handleUrlClicks(onClicked: ((String) -> Unit)? = null) {
		//create span builder and replaces current text with it
		text = SpannableStringBuilder.valueOf(text).apply {
			//search for all URL spans and replace all spans with our own clickable spans
			getSpans(0, length, URLSpan::class.java).forEach {
				//add new clickable span at the same position
				setSpan(
					object : ClickableSpan() {
						override fun onClick(widget: View) {
							onClicked?.invoke(it.url)
						}
					},
					getSpanStart(it),
					getSpanEnd(it),
					Spanned.SPAN_INCLUSIVE_EXCLUSIVE
				)
				//remove old URLSpan
				removeSpan(it)
			}
		}
		//make sure movement method is set
		movementMethod = LinkMovementMethod.getInstance()
	}

	fun getDeviceMetric(context: Context): DisplayMetrics {
		val metric = DisplayMetrics()
		(context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager)?.apply {
			this.defaultDisplay.getMetrics(metric)
		}
		return metric
	}

}