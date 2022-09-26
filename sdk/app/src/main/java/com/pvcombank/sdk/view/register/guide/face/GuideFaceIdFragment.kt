package com.pvcombank.sdk.view.register.guide.face

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pvcombank.sdk.base.PVFragment
import com.pvcombank.sdk.databinding.FragmentGuideFaceCaptureBinding
import com.pvcombank.sdk.model.MasterModel
import com.pvcombank.sdk.view.register.confirm.InformationConfirmFragment
import com.trustingsocial.tvcoresdk.external.*
import com.trustingsocial.tvsdk.TrustVisionSDK

class GuideFaceIdFragment : PVFragment<FragmentGuideFaceCaptureBinding>() {
	override fun onBack(): Boolean = false
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
			topBar.setTitle("Hướng dẫn quay chân dung")
			btnConfirm.setOnClickListener {
				startFaceCapture()
			}
		}
	}
	
	override fun onStart() {
		super.onStart()
		if (MasterModel.getInstance().captureFaceState == "capture_success"){
			openFragment(
				InformationConfirmFragment::class.java,
				Bundle(),
				true
			)
		}
	}
	private fun startFaceCapture() {
		val config = TVSelfieConfiguration.Builder()
			.setCameraOption(TVSDKConfiguration.TVCameraOption.FRONT)
			.setEnableSound(true)
			.setLivenessMode(TVSDKConfiguration.TVLivenessMode.PASSIVE)
			.setSkipConfirmScreen(false)
		TrustVisionSDK.startSelfieCapturing(
			requireActivity(),
			config.build(),
			object : TVCapturingCallBack() {
				override fun onError(p0: TVDetectionError?) {
					println("Error: $p0")
				}
				
				override fun onSuccess(p0: TVDetectionResult?) {
					println("Success $p0")
					MasterModel.getInstance().captureFaceState = "capture_success"
				}
				
				override fun onCanceled() {
					println("Cancel")
				}
				
				override fun onNewFrameBatch(p0: FrameBatch) {
					super.onNewFrameBatch(p0)
					println("New frame: $p0")
				}
			}
		)
	}
}