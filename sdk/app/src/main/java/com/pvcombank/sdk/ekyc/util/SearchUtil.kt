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
}