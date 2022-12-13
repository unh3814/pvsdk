package com.pvcombank.sdk.model

class MasterModel {
	companion object {
		@JvmStatic
		private var INSTANCE: MasterModel? = null
		
		fun getInstance(): MasterModel {
			return INSTANCE ?: MasterModel().apply {
				INSTANCE = this
			}
		}
	}

	private val cache = hashMapOf<String, Any>()
}