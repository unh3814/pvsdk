package com.pvcombank.sdk.ekyc.view.register.guide.face

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.pvcombank.sdk.ekyc.R
import com.pvcombank.sdk.ekyc.base.PVFragment
import com.pvcombank.sdk.ekyc.databinding.FragmentGuideFaceCaptureBinding
import com.pvcombank.sdk.ekyc.model.Constants
import com.pvcombank.sdk.ekyc.model.MasterModel
import com.pvcombank.sdk.ekyc.model.request.Gesture
import com.pvcombank.sdk.ekyc.model.request.RequestVerifySelfies
import com.pvcombank.sdk.ekyc.model.response.ResponseOCR
import com.pvcombank.sdk.ekyc.repository.OnBoardingRepository
import com.pvcombank.sdk.ekyc.util.execute.MyExecutor
import com.pvcombank.sdk.ekyc.view.popup.AlertPopup
import com.pvcombank.sdk.ekyc.view.register.after_create.AfterCreateFragment
import com.pvcombank.sdk.ekyc.view.register.confirm.InformationConfirmFragment
import com.pvcombank.sdk.ekyc.view.register.home.HomeFragment
import com.trustingsocial.tvcoresdk.external.*
import com.trustingsocial.tvsdk.TrustVisionSDK
import java.io.ByteArrayOutputStream
import java.util.*

class GuideFaceIdFragment : PVFragment<FragmentGuideFaceCaptureBinding>() {
	override fun onBack(): Boolean = false
	private val repository = OnBoardingRepository()
	private var count = 0
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
			topBar.setTitle("Hướng dẫn quay video chân dung")
			btnConfirm.setOnClickListener {
				startFaceCapture()
				count++
			}
		}
	}
	
	private fun startFaceCapture() {
		if (count>=5){
			AlertPopup.show(
				fragmentManager = childFragmentManager,
				message = "Quý khách vui lòng thực hiện lại. Chi tiết liên hệ: 1900555592",
				primaryButtonListener = object : AlertPopup.PrimaryButtonListener{
					override fun onClickListener(v: View) {
						openFragment(
							AfterCreateFragment::class.java,
							Bundle(),
							false
						)
					}
				},
				primaryTitle = getString(R.string.txt_close)
			)
			return
		}
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
										message = "Có lỗi xảy ra vui lòng thực hiện lại",
										primaryTitle = getString(R.string.txt_close),
										primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
											override fun onClickListener(v: View) {
												startFaceCapture()
												count++
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
									message = "$message",
									primaryTitle = getString(R.string.txt_close),
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
								message = responseSuccess.errorMessage ?: message,
								primaryTitle = getString(R.string.txt_close),
								primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
									override fun onClickListener(v: View) {
										startFaceCapture()
										count++
									}
								}
							)
						}
						else -> {
							AlertPopup.show(
								fragmentManager = childFragmentManager,
								message = responseSuccess.errorMessage ?: message,
								primaryTitle = getString(R.string.txt_close),
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
						message = responseSuccess.errorMessage,
						primaryTitle = getString(R.string.txt_close),
						primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
							override fun onClickListener(v: View) {
							}
						}
					)
				}
			}
	}
}