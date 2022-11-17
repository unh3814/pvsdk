package com.pvcombank.sdk.ekyc.view.register.guide.card

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.pvcombank.sdk.ekyc.R
import com.pvcombank.sdk.ekyc.base.PVFragment
import com.pvcombank.sdk.ekyc.databinding.FragmentGuideCardCaptureBinding
import com.pvcombank.sdk.ekyc.model.Constants
import com.pvcombank.sdk.ekyc.model.Constants.EKYC_DONE
import com.pvcombank.sdk.ekyc.model.MasterModel
import com.pvcombank.sdk.ekyc.model.request.CheckAccountRequest
import com.pvcombank.sdk.ekyc.model.response.ResponseOCR
import com.pvcombank.sdk.ekyc.repository.OnBoardingRepository
import com.pvcombank.sdk.ekyc.util.FileUtils.toFile
import com.pvcombank.sdk.ekyc.util.Utils.toTypeId
import com.pvcombank.sdk.ekyc.util.execute.MyExecutor
import com.pvcombank.sdk.ekyc.view.popup.AlertPopup
import com.pvcombank.sdk.ekyc.view.register.after_create.AfterCreateFragment
import com.pvcombank.sdk.ekyc.view.register.guide.face.GuideFaceIdFragment
import com.pvcombank.sdk.ekyc.view.register.home.HomeFragment
import com.trustingsocial.tvcoresdk.external.*
import com.trustingsocial.tvcoresdk.external.TVSDKConfiguration.TVCardSide
import com.trustingsocial.tvsdk.TrustVisionSDK
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class GuideCardIdFragment : PVFragment<FragmentGuideCardCaptureBinding>() {
	override fun onBack(): Boolean {
		openFragment(
			AfterCreateFragment::class.java,
			Bundle()
		)
		return true
	}
	
	private val repository = OnBoardingRepository()
	private val typeCard: String get() = requireArguments().getString("type_card") ?: ""
	private val phoneNumber = (MasterModel.getInstance().cache["phone_number"] as? String) ?: ""
	private var count = 0
	private var currentStep = TVCardSide.FRONT
	private var idNumber = ""
	private val hashMapImage = hashMapOf<String, Pair<Boolean, Bitmap?>>()
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
				startCaptureCard(currentStep)
			}
			repository.error.observe(
				viewLifecycleOwner,
				Observer {
					hideLoading()
					it?.let {
						AlertPopup.show(
							fragmentManager = childFragmentManager,
							message = it.second,
							primaryTitle = getString(R.string.txt_close),
							primaryButtonListener = object : AlertPopup.PrimaryButtonListener{
								override fun onClickListener(v: View) {
									if (it.first in 401..499){
										openFragment(
											AfterCreateFragment::class.java,
											Bundle()
										)
									}
								}
							}
						)
					}
				}
			)
		}
	}
	
	fun startCaptureCard(cardSide: TVCardSide? = TVCardSide.FRONT) {
		currentStep = cardSide!!
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
		var file: File? = null
		var type = ""
		
		hashMapImage["front"] = Pair(false, tvDetectionResult.frontCardImage?.image)
		hashMapImage["back"] = Pair(false, tvDetectionResult.backCardImage?.image)
		if (hashMapImage["front"]?.first == false && hashMapImage["front"]?.second != null) {
			file = hashMapImage["front"]?.second?.toFile(requireContext())
			type = "front"
		} else if (hashMapImage["back"]?.first == false && hashMapImage["back"]?.second != null) {
			file = hashMapImage["back"]?.second?.toFile(requireContext())
			type = "back"
		} else {
			AlertPopup.show(
				fragmentManager = childFragmentManager,
				message = "Đã có lỗi trong quá trình xử lý hình ảnh, vui lòng thử lại sau",
				primaryTitle = getString(R.string.txt_close)
			)
		}
		startVerify(file!!, type) { response ->
			val responseSuccess = response["success"]
			if (responseSuccess is ResponseOCR) {
				val label = responseSuccess.cardLabel
				if (label?.isNotEmpty() == true) {
					requireArguments().putString("type_card", label)
				}
				count++
				if (responseSuccess.error.isNullOrEmpty()) {
					hashMapImage[type]?.copy(first = true)
					handlerSuccess(responseSuccess)
				} else {
					hideLoading()
					if (count >= 5){
						AlertPopup.show(
							fragmentManager = childFragmentManager,
							message = "Quý khách vui lòng thực hiện lại. Chi tiết liên hệ: 1900555592",
							primaryButtonListener = object : AlertPopup.PrimaryButtonListener{
								override fun onClickListener(v: View) {
									openFragment(
										AfterCreateFragment::class.java,
										Bundle()
									)
								}
							},
							primaryTitle = getString(R.string.txt_close)
						)
					} else {
						handlerError(responseSuccess)
					}
				}
			}
			val responseFail = response["fail"]
			if (responseFail is String) {
				val isEndAuth = responseFail.contains("403") || responseFail.contains("401")
				var message = responseFail
				when {
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
								openFragment(HomeFragment::class.java, arguments ?: Bundle())
							}
						}
					}
				)
			}
		}
	}
	
	private fun handlerSuccess(responseSuccess: ResponseOCR?) {
		if (!responseSuccess?.idNumber.isNullOrEmpty()){
			idNumber = responseSuccess?.idNumber ?: ""
		}
		checkAccount(
			idNumber = idNumber,
			idType = responseSuccess?.cardLabel?.toTypeId() ?: "",
			phone = phoneNumber
		) {
			hideLoading()
			if (typeCard.contains("passport")) {
				MasterModel.getInstance().dataOCR["front_card"] = responseSuccess
				MasterModel.getInstance().updateDataOCR()
				openFragment(
					GuideFaceIdFragment::class.java,
					requireArguments()
				)
			}
			if (typeCard.contains("cccd") || typeCard.contains("cmnd")) {
				if (typeCard.contains("front")) {
					MasterModel.getInstance().dataOCR["front_card"] = responseSuccess
					MasterModel.getInstance().updateDataOCR()
					startCaptureCard(TVCardSide.BACK)
					count--
				}
				if (typeCard.contains("back")) {
					MasterModel.getInstance().dataOCR["back_card"] = responseSuccess
					MasterModel.getInstance().updateDataOCR()
					count--
					openFragment(
						GuideFaceIdFragment::class.java,
						requireArguments()
					)
				}
			}
		}
	}
	
	private fun handlerError(responseSuccess: ResponseOCR) {
		MyExecutor.Default
			.build()
			.executeDefault()
			.execute {
				var message = "" // Cái này không chắc là cần
				var cardSide = TVCardSide.FRONT
				Constants.errorCaptureMap[responseSuccess.error?.trim()]?.apply {
					message = this.second
					when (this.first) {
						Constants.CAPTURE_CURRENT_STEP -> {
							cardSide = if (typeCard.contains("back")) {
								TVCardSide.BACK
							} else {
								TVCardSide.FRONT
							}
						}
						Constants.CAPTURE_FIRST, Constants.CAPTURE_CARD_NOT_MATCH -> {
							cardSide = TVCardSide.FRONT
						}
						EKYC_DONE -> {
							AlertPopup.show(
								fragmentManager = childFragmentManager,
								message = this.second,
								primaryTitle = getString(R.string.txt_close),
								primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
									override fun onClickListener(v: View) {
										requireActivity().finish()
									}
								}
							)
						}
						else -> {
							requireArguments().putString("type_card", null)
							cardSide = TVCardSide.FRONT
						}
					}
					if (message.isNotEmpty()) {
						AlertPopup.show(
							fragmentManager = childFragmentManager,
							message = responseSuccess.errorMessage ?: message,
							primaryTitle = getString(R.string.txt_close),
							primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
								override fun onClickListener(v: View) {
									startCaptureCard(cardSide)
									count++
								}
							}
						)
					}
				} ?: kotlin.run {
					requireArguments().putString("type_card", null)
					AlertPopup.show(
						fragmentManager = childFragmentManager,
						message = responseSuccess.errorMessage,
						primaryTitle = getString(R.string.txt_close),
						primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
							override fun onClickListener(v: View) {
								startCaptureCard(cardSide)
								count++
							}
						}
					)
				}
			}
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
	
	private fun checkAccount(
		idNumber: String,
		idType: String,
		phone: String,
		success: () -> Unit
	) {
		repository.checkAccount(
			request = CheckAccountRequest(
				idNumber = idNumber,
				idType = idType,
				phone = phone
			)
		) {
			hideLoading()
			(it["fail"] as? String)?.let { message ->
				AlertPopup.show(
					fragmentManager = childFragmentManager,
					message = message,
					primaryTitle = getString(R.string.txt_close),
					primaryButtonListener = object : AlertPopup.PrimaryButtonListener{
						override fun onClickListener(v: View) {
						
						}
					}
				)
			}
			(it["error_network"] as? String)?.let { message ->
				AlertPopup.show(
					fragmentManager = childFragmentManager,
					message = message,
					primaryTitle = getString(R.string.txt_close),
					primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
						override fun onClickListener(v: View) {
							requireActivity().finish()
						}
					}
				)
			}
			
			(it["next"] as? String)?.let {
				success.invoke()
			}
			(it["stop"] as? String)?.let { message ->
				AlertPopup.show(
					fragmentManager = childFragmentManager,
					message = message,
					primaryTitle = getString(R.string.txt_close),
					primaryButtonListener = object : AlertPopup.PrimaryButtonListener{
						override fun onClickListener(v: View) {
						
						}
					}
				)
			}
		}
	}
}