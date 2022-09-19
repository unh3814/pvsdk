package com.pvcombank.sdk.view.register.scan.face

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.gesture.Gesture
import com.otaliastudios.cameraview.gesture.GestureAction
import com.pvcombank.sdk.base.PVFragment
import com.pvcombank.sdk.databinding.FragmentFaceCaptureBinding

class FaceCaptureFragment : PVFragment<FragmentFaceCaptureBinding>() {
	private val permissionList = listOf(
		Manifest.permission.CAMERA,
		Manifest.permission.RECORD_AUDIO,
		Manifest.permission.READ_EXTERNAL_STORAGE,
		Manifest.permission.WRITE_EXTERNAL_STORAGE
	)
	private val isAllPermissionGranted
		get() = permissionList.all {
			ContextCompat.checkSelfPermission(
				requireContext(),
				it
			) == PackageManager.PERMISSION_GRANTED
		}
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		viewBinding = FragmentFaceCaptureBinding.inflate(inflater, container, false)
		return viewBinding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewBinding.apply {
			viewCamera.mapGesture(Gesture.TAP, GestureAction.AUTO_FOCUS)
			viewCamera.addCameraListener(object : CameraListener() {
				override fun onVideoRecordingStart() {
					super.onVideoRecordingStart()
					
				}
			})
			viewCamera.addFrameProcessor {
				Log.d("CAMERA SOURCE", it.getData())
			}
			imgCapture.setOnClickListener {
				viewCamera.takePicture()
			}
			checkPermissionCamera()
		}
	}
	
	
	private fun checkPermissionCamera() {
		if (!isAllPermissionGranted) {
			registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
				if (result.all { it.value }) {
					viewBinding.viewCamera.setLifecycleOwner(this)
					viewBinding.viewCamera.open()
				} else {
					requireActivity().supportFragmentManager.popBackStack()
				}
			}.launch(permissionList.toTypedArray())
		} else {
			viewBinding.viewCamera.setLifecycleOwner(this)
			viewBinding.viewCamera.open()
		}
	}
	
	private fun createCameraSource() {
		// High-accuracy landmark detection and face classification
		val highAccuracyOpts = FaceDetectorOptions.Builder()
			.setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
			.setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
			.setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
			.build()

		// Real-time contour detection
		val realTimeOpts = FaceDetectorOptions.Builder()
			.setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
			.build()
	}
	
	override fun onBack(): Boolean = false
}