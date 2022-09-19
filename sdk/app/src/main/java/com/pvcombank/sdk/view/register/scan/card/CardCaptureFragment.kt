package com.pvcombank.sdk.view.register.scan.card

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraOptions
import com.otaliastudios.cameraview.CameraUtils
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.gesture.Gesture
import com.otaliastudios.cameraview.gesture.GestureAction
import com.pvcombank.sdk.base.PVFragment
import com.pvcombank.sdk.databinding.FragmentCardCaptureBinding
import com.pvcombank.sdk.util.FileUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject

class CardCaptureFragment : PVFragment<FragmentCardCaptureBinding>() {
	companion object {
		const val STATE_CAPTURE_CARD_FRONT = "state.camera.card.front"
		const val STATE_CAPTURE_CARD_BEHIND = "state.camera.card.behind"
		const val STATE_CAPTURE_DONE = "state.camera.card.done"
	}
	
	private val pictureResult: PublishSubject<PictureResult> = PublishSubject.create()
	private var stateCapture: String = STATE_CAPTURE_CARD_FRONT
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
	): View {
		viewBinding = FragmentCardCaptureBinding.inflate(inflater, container, false)
		return viewBinding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewBinding.apply {
			viewCamera.mapGesture(Gesture.TAP, GestureAction.AUTO_FOCUS)
			viewCamera.addCameraListener(object : CameraListener() {
				override fun onCameraOpened(options: CameraOptions) {
					super.onCameraOpened(options)
				}
				
				override fun onCameraClosed() {
					super.onCameraClosed()
				}
				
				override fun onPictureTaken(result: PictureResult) {
					super.onPictureTaken(result)
					pictureResult.onNext(result)
				}
			})
			imgCapture.setOnClickListener {
				viewCamera.takePicture()
			}
			showTextWithState(stateCapture)
			handlerPictureResult()
			checkPermissionCamera()
		}
	}
	
	private fun handlerCardResult(){
//		vm.responseObserver
//			.observeOn(Schedulers.newThread())
//			.subscribeOn(AndroidSchedulers.mainThread())
//			.subscribe{
//			}
	}
	
	private fun handlerPictureResult() {
		pictureResult.observeOn(AndroidSchedulers.mainThread())
			.subscribeOn(Schedulers.newThread())
			.map {
				val bitmap = CameraUtils.decodeBitmap(
					it.data,
					800,
					600,
					BitmapFactory.Options()
				)
				bitmap?.let {
					when (stateCapture) {
						STATE_CAPTURE_CARD_BEHIND -> {
							FileUtils.saveFile(
								requireContext(),
								bitmap,
								FileUtils.cardBackFileName
							)
						}
						STATE_CAPTURE_CARD_FRONT -> {
							FileUtils.saveFile(
								requireContext(),
								bitmap,
								FileUtils.cardFileName
							)
						}
					}
				} ?: kotlin.run {
				}
				bitmap!!
			}
			.subscribe(
				{
					when (stateCapture) {
						STATE_CAPTURE_CARD_FRONT -> {
							stateCapture = STATE_CAPTURE_CARD_BEHIND
//							vm.getCardInfo(
//								FileUtils.getCardFile(requireContext()),
//								STATE_CAPTURE_CARD_FRONT
//							)
						}
						STATE_CAPTURE_CARD_BEHIND -> {
							stateCapture = STATE_CAPTURE_DONE
//							vm.getCardInfo(
//								FileUtils.getCardBackFile(requireContext()),
//								STATE_CAPTURE_CARD_BEHIND
//							)
						}
					}
					showTextWithState(stateCapture)
					hideLoading()
				},
				{
					Log.e("CAMERA", "Handler error: ${it.message}")
				}
			)
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
	
	private fun showTextWithState(state: String) {
		when (state) {
			STATE_CAPTURE_CARD_FRONT -> {
				viewBinding.tvTitleAlertCapture.text = "Căn mặt trước vào khung"
			}
			STATE_CAPTURE_CARD_FRONT -> {
				viewBinding.tvTitleAlertCapture.text = "Căn mặt sau vào khung"
			}
			STATE_CAPTURE_DONE -> {
				viewBinding.tvTitleAlertCapture.text = "Hoàn thành"
			}
		}
	}
	
	override fun onBack(): Boolean = false
}