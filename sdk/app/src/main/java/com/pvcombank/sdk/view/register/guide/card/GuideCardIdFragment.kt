package com.pvcombank.sdk.view.register.guide.card

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pvcombank.sdk.base.PVFragment
import com.pvcombank.sdk.databinding.FragmentGuideCardCaptureBinding
import com.pvcombank.sdk.model.Constants
import com.pvcombank.sdk.model.MasterModel
import com.pvcombank.sdk.model.response.ResponseOCR
import com.pvcombank.sdk.repository.OnBoardingRepository
import com.pvcombank.sdk.util.FileUtils.toFile
import com.pvcombank.sdk.view.popup.AlertPopup
import com.pvcombank.sdk.view.register.guide.face.GuideFaceIdFragment
import com.trustingsocial.tvcoresdk.external.*
import com.trustingsocial.tvcoresdk.external.TVSDKConfiguration.TVCardSide
import com.trustingsocial.tvsdk.TrustVisionSDK
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class GuideCardIdFragment : PVFragment<FragmentGuideCardCaptureBinding>() {
	override fun onBack(): Boolean = false
	private val repository = OnBoardingRepository()
	private val typeCard: String get() = arguments?.getString("type_card") ?: ""
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		viewBinding = FragmentGuideCardCaptureBinding.inflate(inflater, container, false)
		return viewBinding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewBinding.apply {
			topBar.show()
			topBar.setTitle("Hướng dẫn chụp ảnh")
			MasterModel.getInstance().ocrFromOTP = ResponseOCR()
			btnConfirm.setOnClickListener {
				//Start Scan cardID
				startCaptureCard()
			}
		}
	}
	
	fun startCaptureCard(cardSide: TVCardSide? = TVCardSide.FRONT) {
		val nationalIdCard = TVCardType(
			"vn.national_id",
			"CMND cũ / CMND mới / CCCD",
			true,
			TVCardType.TVCardOrientation.HORIZONTAL
		)
		val builder = TVIDConfiguration.Builder()
			.setCardType(nationalIdCard)
			.setEnableSound(true)
			.setReadBothSide(false)
			.setEnablePhotoGalleryPicker(false)
			.setEnableTiltChecking(false)
			.setSkipConfirmScreen(true)
			.setCardSide(cardSide)
		TrustVisionSDK.startIDCapturing(
			requireActivity(),
			builder.build(),
			object : TVCapturingCallBack() {
				override fun onError(error: TVDetectionError) {
					Log.e("TVSDK Error", "p0")
				}
				
				override fun onSuccess(tvDetectionResult: TVDetectionResult) {
					Log.e("TVSDK Success", "p0")
					handlerSuccessCapturing(tvDetectionResult)
				}
				
				override fun onCanceled() {
					Log.e("TVSDK Cancel", "p0")
				}
				
				override fun onNewFrameBatch(frameBatch: FrameBatch) {
					super.onNewFrameBatch(frameBatch)
					Log.e("frameBatch", frameBatch.id)
				}
			})
	}
	
	private fun handlerSuccessCapturing(tvDetectionResult: TVDetectionResult) {
		val file: File
		val type = if (typeCard.contains("front")) {
			file = tvDetectionResult.backCardImage.image.toFile(requireContext())
			"back"
		} else {
			file = tvDetectionResult.frontCardImage.image.toFile(requireContext())
			"front"
		}
		startVerify(file, type) { response ->
			hideLoading()
			val responseSuccess = response["success"]
			if (responseSuccess is ResponseOCR) {
				val label = responseSuccess.cardLabel
				if (label?.isNotEmpty() == true) {
					arguments?.putString("type_card", label)
				}
				if (responseSuccess.error?.isNotEmpty() != true) {
					handlerSuccess(responseSuccess)
				} else {
					handlerError(responseSuccess)
				}
			}
			val responseFail = response["fail"]
			if (responseFail is String) {
				//Lỗi api hoặc lỗi internet
				AlertPopup.show(
					fragmentManager = childFragmentManager,
					title = "Thông báo",
					message = "$responseFail",
					primaryTitle = "OK",
					primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
						override fun onClickListener(v: View) {
						
						}
					}
				)
			}
		}
	}
	
	private fun handlerSuccess(responseSuccess: ResponseOCR?) {
		if (typeCard.contains("passport")) {
			MasterModel.getInstance().dataOCR["front_card"] = responseSuccess
			MasterModel.getInstance().updateDataOCR()
			openFragment(
				GuideFaceIdFragment::class.java,
				arguments ?: Bundle(),
				false
			)
		}
		if (typeCard.contains("cccd") || typeCard.contains("cmnd")) {
			if (typeCard.contains("front")) {
				MasterModel.getInstance().dataOCR["front_card"] = responseSuccess
				MasterModel.getInstance().updateDataOCR()
				startCaptureCard(TVCardSide.BACK)
			}
			if (typeCard.contains("back")) {
				MasterModel.getInstance().dataOCR["back_card"] = responseSuccess
				MasterModel.getInstance().updateDataOCR()
				openFragment(
					GuideFaceIdFragment::class.java,
					arguments ?: Bundle(),
					false
				)
			}
		}
	}
	
	private fun handlerError(responseSuccess: ResponseOCR) {
		val cardSite: TVCardSide = when (responseSuccess.error) {
			in Constants.RECAPTURE_CURRENT_STEP -> {
				if (typeCard.contains("back")) {
					TVCardSide.BACK
				} else {
					TVCardSide.FRONT
				}
			}
			else -> {
				TVCardSide.FRONT
			}
		}
		AlertPopup.show(
			fragmentManager = childFragmentManager,
			title = "Thông báo",
			message = "${responseSuccess.errorMessage}",
			primaryTitle = "OK",
			primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
				override fun onClickListener(v: View) {
					startCaptureCard(cardSite)
				}
			}
		)
	}
	
	private fun startVerify(file: File, type: String, callBack: (HashMap<String, Any>) -> Unit) {
		showLoading()
		val mediaBackType = "application/octet-stream".toMediaTypeOrNull()
		val body = MultipartBody.Builder()
			.setType(MultipartBody.FORM)
			.addFormDataPart(
				"files",
				"${file.name}.jpg",
				RequestBody.create(mediaBackType, file)
			)
			.addFormDataPart("label", type)
			.build()
		repository.verifyCard(body, callBack)
	}
}