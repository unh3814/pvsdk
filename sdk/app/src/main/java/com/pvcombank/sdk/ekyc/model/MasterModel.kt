package com.pvcombank.sdk.ekyc.model

import com.pvcombank.sdk.ekyc.model.response.ResponseOCR
import com.trustingsocial.tvcoresdk.external.FrameBatch
import io.reactivex.rxjava3.subjects.PublishSubject

class MasterModel {
	var clientSecret: String? = null
	var appUnitID: String? = null
	var uuidOfOTP: String? = null
	var timeLogin: Long? = null

	lateinit var errorString: PublishSubject<String>
	lateinit var successString: PublishSubject<String>
	val frameBatch = mutableListOf<FrameBatch>()
	val dataOCR = hashMapOf<String, ResponseOCR?>()
	var selectBranch: BranchModel? = null
	var ocrFromOTP: ResponseOCR = ResponseOCR()
	val cache = hashMapOf<String, Any>()
	var isProduction = false
	companion object {
		@JvmStatic
		private var INSTANCE: MasterModel? = null
		
		fun getInstance(): MasterModel {
			return INSTANCE ?: MasterModel().apply {
				INSTANCE = this
			}
		}
	}
	
	fun updateDataOCR(){
		val front = dataOCR["front_card"]
		val back = dataOCR["back_card"]
		ocrFromOTP.apply {
			cardImageId = front?.cardImageId ?: back?.cardImageId
			cardLabel = front?.cardLabel ?: back?.cardLabel
			cardType = front?.cardType ?: back?.cardType
			cifNumber = front?.cifNumber ?: back?.cifNumber
			dob = front?.dob ?: back?.dob
			email = front?.email ?: back?.email
			error = front?.error ?: back?.error
			expDate = front?.expDate ?: back?.expDate
			gender = front?.gender ?: back?.gender
			getCifNumber = front?.getCifNumber ?: back?.getCifNumber
			idNumber = front?.idNumber ?: back?.idNumber
			issueDate = back?.issueDate ?: front?.issueDate
			issuePlace = back?.issuePlace ?: front?.issuePlace
			mobilePhone = front?.mobilePhone ?: back?.mobilePhone
			name = front?.name ?: back?.name
			nationality = front?.nationality ?: back?.nationality
			nativePlace = front?.nativePlace ?: back?.nativePlace
			permanentAddress = front?.permanentAddress ?: back?.permanentAddress
			phone = front?.phone ?: back?.phone
			placeOfBirth = front?.placeOfBirth ?: back?.placeOfBirth
			signature = front?.signature ?: back?.signature
			signatureUpdateTime = front?.signatureUpdateTime ?: back?.signatureUpdateTime
			status = front?.status ?: back?.status
		}
	}
	
	fun getDataOCR(): ResponseOCR {
		return ocrFromOTP
	}
	
	fun cleanOCR(){
		frameBatch.clear()
		dataOCR.clear()
		ocrFromOTP = ResponseOCR()
	}
}