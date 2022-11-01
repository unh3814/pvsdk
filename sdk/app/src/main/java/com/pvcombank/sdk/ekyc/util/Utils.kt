package com.pvcombank.sdk.ekyc.util

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pvcombank.sdk.ekyc.model.*
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
		addBackStack: Boolean
	) {
		val hostFragmentID = (this as com.pvcombank.sdk.ekyc.base.PVActivity<*>).fragmentHostID
		supportFragmentManager.beginTransaction()
			.replace(hostFragmentID, clazz, arg)
			.addToBackStack(clazz.simpleName)
			.setReorderingAllowed(addBackStack)
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
	
	fun String.toPVDate(): String {
		val f1 = SimpleDateFormat("dd/MM/yyyy")
		val f2 = SimpleDateFormat("yyyy-MM-dd")
		return f1.format(f2.parse(this))
	}
	
	fun Date.toPVDate(): String {
		val f2 = SimpleDateFormat("dd/MM/yyyy")
		val f1 = SimpleDateFormat("yyyy-MM-dd")
		return f2.format(this)
	}
	
	fun Date.toSVDate(): String{
		val format = SimpleDateFormat("yyyy-MM-dd")
		return format.format(this)
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
	
	fun checkOutOfTime(): Boolean {
		MasterModel.getInstance().timeLogin?.let {
			val currentTime = Date().time
			val `10Minute` = 600000
			return (currentTime - it) >= `10Minute`
		}
		return false
	}
	
	fun String.phoneHide(): String {
		val result = java.lang.StringBuilder()
		if (this.isEmpty()) return ""
		this.forEachIndexed { index, c ->
			if (index in 3..6) {
				result.append("*")
			} else {
				result.append(c)
			}
		}
		return result.toString()
	}
	
	fun getListBranch(): List<BranchModel>? {
		return Gson().fromJson<List<BranchModel>>(
			Constants.mockupBranch,
			object : TypeToken<List<BranchModel>>() {}.type
		)
	}
	
	fun String.toTypeId(): String? {
		return when (this) {
			"vn.cmnd_old.front" -> CMND.OLD_FRONT.getData()
			"vn.cmnd_old.back" -> CMND.OLD_BACK.getData()
			"vn.cmnd_new.front" -> CMND.NEW_FRONT.getData()
			"vn.cmnd_new.back" -> CMND.NEW_BACK.getData()
			"vn.cccd.front" -> CCCD.OLD_FRONT.getData()
			"vn.cccd.back" -> CCCD.OLD_BACK.getData()
			"vn.cccd_new.front" -> CCCD.NEW_FRONT.getData()
			"vn.cccd_new.back" -> CCCD.NEW_BACK.getData()
			"vn.passport.front" -> PASSPORT.FRONT.getData()
			else -> null
		}
	}
}
