package com.pvcombank.sdk.view.register.guide.face

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pvcombank.sdk.base.PVFragment
import com.pvcombank.sdk.databinding.FragmentGuideFaceCaptureBinding
import com.pvcombank.sdk.model.MasterModel
import com.pvcombank.sdk.model.request.Gesture
import com.pvcombank.sdk.model.request.RequestVerifySelfies
import com.pvcombank.sdk.model.response.ResponseOCR
import com.pvcombank.sdk.repository.OnBoardingRepository
import com.pvcombank.sdk.view.register.confirm.InformationConfirmFragment
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
						if (result.selfieImages.any{it.frontalImage == null})
						{
							hideLoading()
							showToastMessage("Vui lòng thực hiện lại")
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
							selfieImage.frontalImage?.image?.let { bitmap ->
								val bos = ByteArrayOutputStream()
								bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
								val base64 =
									Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT)
								listFrontal.add(base64)
								listGesture.add(
									Gesture(
										base64 = base64,
										gesture = selfieImage.gesture.lowercase(Locale.getDefault())
									)
								)
							}
						}
						val requestModel = RequestVerifySelfies(
							frontal = listFrontal,
							gesture = listGesture,
							videos = listFrame
						)
						repository.verifySelfies(requestModel) {
							hideLoading()
							if (it["success"] is ResponseOCR) {
								openFragment(
									InformationConfirmFragment::class.java,
									arguments ?: Bundle(),
									true
								)
							}
							if (it["fail"] is String){
								showToastMessage(it["fail"] as String)
								if (it.contains("403")){
									requireActivity().recreate()
								}
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
}