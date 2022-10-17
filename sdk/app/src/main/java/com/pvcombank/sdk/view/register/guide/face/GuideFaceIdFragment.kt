package com.pvcombank.sdk.view.register.guide.face

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.pvcombank.sdk.base.PVFragment
import com.pvcombank.sdk.databinding.FragmentGuideFaceCaptureBinding
import com.pvcombank.sdk.model.Constants
import com.pvcombank.sdk.model.MasterModel
import com.pvcombank.sdk.model.request.Gesture
import com.pvcombank.sdk.model.request.RequestVerifySelfies
import com.pvcombank.sdk.model.response.ResponseOCR
import com.pvcombank.sdk.repository.OnBoardingRepository
import com.pvcombank.sdk.util.execute.MyExecutor
import com.pvcombank.sdk.view.popup.AlertPopup
import com.pvcombank.sdk.view.register.confirm.InformationConfirmFragment
import com.pvcombank.sdk.view.register.home.HomeFragment
import com.trustingsocial.tvcoresdk.external.*
import com.trustingsocial.tvsdk.TrustVisionSDK
import java.io.ByteArrayOutputStream
import java.util.*

class GuideFaceIdFragment : PVFragment<FragmentGuideFaceCaptureBinding>() {
	override fun onBack(): Boolean = false
	private val repository = OnBoardingRepository()
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		viewBinding = FragmentGuideFaceCaptureBinding.inflate(inflater, container, false)
		return viewBinding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewBinding.apply {
			topBar.show()
			topBar.setTitle("Hướng dẫn quay chân dung")
			btnConfirm.setOnClickListener {
				startFaceCapture()
			}
		}
	}
	
	private fun startFaceCapture() {
		val config = TVSelfieConfiguration.Builder()
			.setCameraOption(TVSDKConfiguration.TVCameraOption.FRONT)
			.setEnableSound(true)
			.setLivenessMode(TVSDKConfiguration.TVLivenessMode.ACTIVE)
			.setSkipConfirmScreen(true)
		TrustVisionSDK.startSelfieCapturing(
			requireActivity(),
			config.build(),
			object : TVCapturingCallBack() {
				override fun onError(p0: TVDetectionError?) {
					println("Error: $p0")
				}
				
				override fun onSuccess(tvDetectionResult: TVDetectionResult?) {
					tvDetectionResult?.let { result ->
						showLoading()
						if (result.selfieImages.any { it.frontalImage == null }) {
							hideLoading()
							handler.postDelayed(
								{
									AlertPopup.show(
										fragmentManager = childFragmentManager,
										title = "Thông báo",
										message = "Có lỗi xảy ra vui lòng thực hiện lại",
										primaryTitle = "OK",
										primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
											override fun onClickListener(v: View) {
												startFaceCapture()
											}
										}
									)
								}, 300L
							)
							return
						}
						val listFrame = mutableListOf<TVFrameClass>()
						MasterModel.getInstance().frameBatch.forEach {
							if (result.selfieFrameBatchIds.contains(it.id)) {
								listFrame.addAll(it.frames)
							}
						}
						val listGesture = mutableListOf<Gesture>()
						val listFrontal = mutableListOf<String>()
						result.selfieImages.forEach { selfieImage ->
							selfieImage.gestureImage?.image?.let { bitmap ->
								val bos = ByteArrayOutputStream()
								bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
								val base64 =
									Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT)
										.replace("\n", "")
								listGesture.add(
									Gesture(
										base64 = base64,
										gesture = selfieImage.gesture.lowercase(Locale.getDefault())
									)
								)
							}
							selfieImage.frontalImage?.image?.let { bitmap ->
								val bos = ByteArrayOutputStream()
								bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
								val base64 =
									Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT)
										.replace("\n", "")
								listFrontal.add(base64)
							}
						}
						
						val requestModel = RequestVerifySelfies(
							frontal = listFrontal,
							videos = listFrame,
							gesture = listGesture
						)
						repository.verifySelfies(requestModel) {
							hideLoading()
							val responseSuccess = it["success"]
							if (responseSuccess is ResponseOCR) {
								if (responseSuccess.error == null) {
									requireArguments().putBoolean("hide_back", false)
									openFragment(
										InformationConfirmFragment::class.java,
										requireArguments(),
										true
									)
								} else {
									handlerError(responseSuccess)
								}
							}
							val responseError = it["fail"]
							if (responseError is String) {
								val isEndAuth = responseError.contains("403") || responseError.contains("401")
								var message = responseError
								when{
									isEndAuth -> {
										message = "Phiên làm việc hết hạn."
									}
								}
								AlertPopup.show(
									fragmentManager = childFragmentManager,
									title = "Thông báo",
									message = "$message",
									primaryTitle = "OK",
									primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
										override fun onClickListener(v: View) {
											if (isEndAuth) {
												requireActivity().supportFragmentManager.popBackStack(
													null,
													FragmentManager.POP_BACK_STACK_INCLUSIVE
												)
												openFragment(
													HomeFragment::class.java,
													arguments ?: Bundle(),
													true
												)
											}
										}
									}
								)
							}
						}
					}
				}
				
				override fun onCanceled() {
					println("Cancel")
				}
				
				override fun onNewFrameBatch(p0: FrameBatch) {
					super.onNewFrameBatch(p0)
					MasterModel.getInstance().frameBatch.add(p0)
				}
				
			}
		)
	}
	
	private fun handlerError(responseSuccess: ResponseOCR) {
		MyExecutor.Default
			.build()
			.executeDefault()
			.execute {
				Constants.errorCaptureMap[responseSuccess.error]?.apply {
					val message = this.second
					when (this.first) {
						Constants.CAPTURE_FACE -> {
							AlertPopup.show(
								fragmentManager = childFragmentManager,
								title = "Thông báo",
								message = responseSuccess.errorMessage ?: message,
								primaryTitle = "OK",
								primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
									override fun onClickListener(v: View) {
										startFaceCapture()
									}
								}
							)
						}
						else -> {
							AlertPopup.show(
								fragmentManager = childFragmentManager,
								title = "Thông báo",
								message = responseSuccess.errorMessage ?: message,
								primaryTitle = "OK",
								primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
									override fun onClickListener(v: View) {
									
									}
								}
							)
						}
					}
				} ?: kotlin.run {
					AlertPopup.show(
						fragmentManager = childFragmentManager,
						title = "Thông báo",
						message = responseSuccess.errorMessage,
						primaryTitle = "OK",
						primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
							override fun onClickListener(v: View) {
							}
						}
					)
				}
			}
	}
}