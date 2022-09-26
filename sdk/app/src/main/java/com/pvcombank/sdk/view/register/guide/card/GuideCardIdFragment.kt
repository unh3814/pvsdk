package com.pvcombank.sdk.view.register.guide.card

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.pvcombank.sdk.base.PVFragment
import com.pvcombank.sdk.databinding.FragmentGuideCardCaptureBinding
import com.pvcombank.sdk.model.Constants
import com.pvcombank.sdk.model.MasterModel
import com.pvcombank.sdk.model.response.ResponseOCR
import com.pvcombank.sdk.repository.OnBoardingRepository
import com.pvcombank.sdk.util.FileUtils.toFile
import com.pvcombank.sdk.view.register.guide.face.GuideFaceIdFragment
import com.trustingsocial.tvcoresdk.external.*
import com.trustingsocial.tvsdk.TrustVisionSDK
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class GuideCardIdFragment : PVFragment<FragmentGuideCardCaptureBinding>() {
	override fun onBack(): Boolean = false
	private val repository = OnBoardingRepository()
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
			topBar.setTitle("Hướng dẫn chụp ảnh")
			btnConfirm.setOnClickListener {
				//Start Scan cardID
				startCaptureCard()
			}
		}
	}
	
	override fun onStart() {
		super.onStart()
		if (MasterModel.getInstance().captureCardIDState == "capture_success") {
			openFragment(
				GuideFaceIdFragment::class.java,
				Bundle(),
				true
			)
		}
	}
	
	private fun initTrustVision() {
		TrustVisionSDK.init(
			Constants.TS_CONFIGURATION,
			"vi",
			null
		)
	}
	
	fun startCaptureCard() {
		val nationalIdCard = TVCardType(
			"vn.national_id",
			"CMND cũ / CMND mới / CCCD",
			true,
			TVCardType.TVCardOrientation.HORIZONTAL
		)
		val builder = TVIDConfiguration.Builder()
			.setCardType(nationalIdCard)
			.setEnableSound(true)
			.setReadBothSide(true)
			.setEnablePhotoGalleryPicker(false)
			.setEnableTiltChecking(false)
			.setSkipConfirmScreen(true)
			.setCardSide(TVSDKConfiguration.TVCardSide.FRONT)
		TrustVisionSDK.startIDCapturing(
			requireActivity(),
			builder.build(),
			object : TVCapturingCallBack() {
				override fun onError(error: TVDetectionError) {
					Log.e("TVSDK Error", "p0")
				}
				
				override fun onSuccess(tvDetectionResult: TVDetectionResult) {
					Log.e("TVSDK Success", "p0")
					val backFile = tvDetectionResult.backCardImage.image.toFile(requireContext())
					val multipartBodyBack =
						backFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
					val backPart =
						MultipartBody.Part.createFormData("BACK", backFile.name, multipartBodyBack)
					val frontFile = tvDetectionResult.frontCardImage.image.toFile(requireContext())
					val multipartBodyFront =
						frontFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
					val frontPart = MultipartBody.Part.createFormData(
						"FRONT",
						frontFile.name,
						multipartBodyFront
					)
					repository.verifyBoldCard(listOf(frontPart, backPart)) {
						if (it["success"] is ResponseOCR) {
							MasterModel.getInstance().captureCardIDState = "capture_success"
						} else {
							MasterModel.getInstance().captureCardIDState = "capture_fail"
						}
					}
				}
				
				override fun onCanceled() {
					Log.e("TVSDK Cancel", "p0")
				}
				
				override fun onNewFrameBatch(frameBatch: FrameBatch) {
					Log.e("TVSDK frameBatch", "p0")
					super.onNewFrameBatch(frameBatch)
					val gson = Gson()
					val framesStr = gson.toJson(frameBatch.getFrames())
					val params: MutableMap<String, Any> = HashMap()
					params["frames"] = framesStr
					params["metadata"] = frameBatch.getMetadata()
					params["label"] = "video"
					
					val jsonToBeUploaded = gson.toJson(params)
				}
			})
	}
}