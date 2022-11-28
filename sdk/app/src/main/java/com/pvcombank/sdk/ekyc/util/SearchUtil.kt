package com.pvcombank.sdk.ekyc.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pvcombank.sdk.ekyc.util.execute.MyExecutor

class SearchUtil<T>(private val data: MutableList<T>, private val searchFunc: SearchFunc<T>) {
	private val executor = MyExecutor.Default.build().executeDefault()
	fun search(value: String): LiveData<List<T>> {
		val result = MutableLiveData<List<T>>()
		executor.execute {
			var tempData = mutableListOf<T>().also {
				it.addAll(data)
			}
			val stringSplit = value.split(",")
			var count = 0
			for (i in (stringSplit.size - 1) downTo 0) {
				val item = stringSplit[i].trim()
				if (item.isEmpty()) continue
				if (count > 1 && !tempData.any { searchFunc.limit(it, item) }) break
				tempData = tempData.filter { searchFunc.filter(it, item) }.toMutableList()
				if (tempData.isEmpty()) break
				count++
			}
			result.postValue(tempData)
		}
		return result
	}
	
	fun updateData(values: MutableList<T>) {
		this.data.clear()
		this.data.addAll(values)
	}
	
	interface SearchFunc<T> {
		fun filter(dataItem: T, stringItem: String): Boolean
		fun limit(dataItem: T, stringItem: String): Boolean
	}
	
	companion object{
		fun convertNonSign(str: String): String? {
			var str = str
			str = str.replace("à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ".toRegex(), "a")
			str = str.replace("è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ".toRegex(), "e")
			str = str.replace("ì|í|ị|ỉ|ĩ".toRegex(), "i")
			str = str.replace("ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ".toRegex(), "o")
			str = str.replace("ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ".toRegex(), "u")
			str = str.replace("ỳ|ý|ỵ|ỷ|ỹ".toRegex(), "y")
			str = str.replace("đ".toRegex(), "d")
			str = str.replace("À|Á|Ạ|Ả|Ã|Â|Ầ|Ấ|Ậ|Ẩ|Ẫ|Ă|Ằ|Ắ|Ặ|Ẳ|Ẵ".toRegex(), "A")
			str = str.replace("È|É|Ẹ|Ẻ|Ẽ|Ê|Ề|Ế|Ệ|Ể|Ễ".toRegex(), "E")
			str = str.replace("Ì|Í|Ị|Ỉ|Ĩ".toRegex(), "I")
			str = str.replace("Ò|Ó|Ọ|Ỏ|Õ|Ô|Ồ|Ố|Ộ|Ổ|Ỗ|Ơ|Ờ|Ớ|Ợ|Ở|Ỡ".toRegex(), "O")
			str = str.replace("Ù|Ú|Ụ|Ủ|Ũ|Ư|Ừ|Ứ|Ự|Ử|Ữ".toRegex(), "U")
			str = str.replace("Ỳ|Ý|Ỵ|Ỷ|Ỹ".toRegex(), "Y")
			str = str.replace("Đ".toRegex(), "D")
			return str
		}
	}
}