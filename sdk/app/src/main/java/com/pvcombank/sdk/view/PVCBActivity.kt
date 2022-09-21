package com.pvcombank.sdk.view

import android.app.AppOpsManager
import android.app.AsyncNotedAppOp
import android.app.SyncNotedAppOp
import android.graphics.drawable.Drawable
import android.os.*
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.pvcombank.sdk.R
import com.pvcombank.sdk.base.PVActivity
import com.pvcombank.sdk.base.PVFragment
import com.pvcombank.sdk.base.model.AlertInline
import com.pvcombank.sdk.base.model.Loading
import com.pvcombank.sdk.base.model.TopBar
import com.pvcombank.sdk.base.model.TopBarListener
import com.pvcombank.sdk.databinding.ActivityPvcbBinding
import com.pvcombank.sdk.util.Utils.openFragment
import com.pvcombank.sdk.view.login.AuthWebLoginFragment
import com.trustingsocial.tvsdk.TrustVisionSDK

class PVCBActivity : PVActivity<ActivityPvcbBinding>() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		viewBinding = ActivityPvcbBinding.inflate(layoutInflater)
		setContentView(viewBinding.root)
		initLoading()
		initAlertInline()
		initTopBar()
		//init TrustVisionSDK
		Handler(Looper.getMainLooper()).post {
			val configuration = "{\"data\":{\"card_types\":[{\"code\":\"vn.national_id\",\"name\":\"CMND cũ / CMND mới / CCCD / Hộ chiếu\",\"orientation\":\"horizontal\",\"has_back_side\":true,\"front_qr\":{\"exist\":false},\"back_qr\":{\"exist\":false}}],\"country\":\"vn\",\"settings\":{\"enable_compare_faces\":true,\"enable_detect_id_card_tampering\":true,\"enable_face_retrieval\":true,\"enable_index_faces\":true,\"enable_read_id_card_info\":true,\"enable_verify_face_liveness\":true,\"enable_verify_id_card_sanity\":true,\"enable_verify_portrait_sanity\":true,\"liveness_modes\":[\"active\",\"passive\"],\"scan_qr\":\"none\",\"sdk_settings\":{\"active_liveness_settings\":{\"face_tracking_setting\":{\"android_terminate_threshold\":0.002847,\"android_warning_threshold\":0.001474,\"enable\":true,\"ios_terminate_threshold\":0.003393,\"ios_warning_threshold\":0.002176,\"limit_for\":\"all_flow\",\"max_interval_ms\":2000,\"max_warning_time\":5,\"web_terminate_threshold\":0.0030152991993743408,\"web_warning_threshold\":0.0017317430600108828},\"flow_interval_time_ms\":3000,\"limit_time_liveness_check\":{\"enable\":true,\"limit_time_second\":45},\"record_video\":{\"enable\":false},\"save_encoded_frames\":{\"enable\":true,\"frames_interval_ms\":180},\"show_gesture_arrow\":true,\"terminate_if_no_face\":{\"enable\":true,\"max_invalid_frame\":5,\"max_time_ms\":1000}},\"id_detection_settings\":{\"auto_capture\":{\"enable\":false,\"show_capture_button\":true},\"blur_check\":{\"enable\":true,\"threshold\":0.29},\"disable_capture_button_if_alert\":true,\"glare_check\":{\"enable\":true,\"threshold\":0.001},\"id_detection\":{\"enable\":true},\"save_frame_settings\":{\"enable\":false,\"frames_interval_ms\":190,\"quality_android\":80,\"quality_ios\":70,\"quality_web\":80},\"scan_qr_settings\":{\"enable\":false,\"limit_time_second\":45}}},\"selfie_camera_options\":[\"front\"],\"selfie_enable_detect_multiple_face\":true,\"support_transaction\":false,\"web_app_crop_face\":\"auto\"}}}"
			TrustVisionSDK.init(
				configuration,
				"vi",
				null
			)
		}
//		if (Build.VERSION_CODES.R < Build.VERSION.SDK_INT) {
//			initOpsManager()
//		}
		viewBinding.apply {
			openFragment(
				AuthWebLoginFragment::class.java,
				Bundle(),
				false
			)
//			Handler(Looper.getMainLooper()).post {
//				TVIDConfiguration.Builder()
//					.setCardSide(TVSDKConfiguration.TVCardSide.FRONT)
//					.setReadBothSide(false)
//					.setReadBothSide(false)
//					.setEnableSound(false)
//					.setEnablePhotoGalleryPicker(false)
//					.setEnableTiltChecking(false)
//					.setSkipConfirmScreen(false)
//					.build()
//					.apply {
//						TrustVisionSDK.startIDCapturing(
//							this@PVCBActivity,
//							this,
//							object : TVCapturingCallBack(){
//								override fun onError(p0: TVDetectionError?) {
//									println(p0)
//								}
//
//								override fun onSuccess(p0: TVDetectionResult?) {
//									println(p0)
//								}
//
//								override fun onCanceled() {
//									println("CANCEL")
//								}
//
//								override fun onNewFrameBatch(p0: FrameBatch) {
//									super.onNewFrameBatch(p0)
//									println(p0)
//								}
//							})
//
//					}
//			}
		}
		supportFragmentManager.addFragmentOnAttachListener { _, fragment ->
			when (fragment) {
				is AuthWebLoginFragment -> {
					viewBinding.topBar.root.visibility = View.GONE
				}
			}
		}
		supportFragmentManager.addOnBackStackChangedListener {
			when (supportFragmentManager.findFragmentById(R.id.hosts_fragment)) {
				is AuthWebLoginFragment, is DialogFragment -> {
					viewBinding.topBar.root.visibility = View.GONE
				}
				else -> {
					viewBinding.topBar.root.visibility = View.VISIBLE
				}
			}
		}
	}
	
	private fun initTopBar() {
		topBar = TopBar.build()
		topBar.setContentView(viewBinding.topBar)
		topBar.apply {
			addBackListener(object : TopBarListener.BackListener {
				override fun onBackClick() {
					goBack()
				}
			})
			addButtonMore(listener = object : TopBarListener.MoreListener{
				override fun onMoreClick() {
					//Do nothing
				}
			})
			show()
		}
	}
	
	private fun initLoading() {
		loading = object : Loading {
			override fun show() {
				viewBinding.loading.visibility = View.VISIBLE
			}
			
			override fun hide() {
				viewBinding.loading.visibility = View.GONE
			}
		}
	}
	
	private fun initAlertInline() {
		alertInline = object : AlertInline {
			override fun show(icon: Drawable?, message: String) {
				viewBinding.apply {
					loInlineMessage.visibility = View.VISIBLE
					tvInlineMessage.text = message
					ivIconInlineMessage.setImageDrawable(
						icon ?: ContextCompat.getDrawable(this@PVCBActivity, R.drawable.ic_untrushed)
					)
					loInlineMessage.isSelected = true
					val countDownTimer = object : CountDownTimer(5000, 1000) {
						override fun onTick(millisUntilFinished: Long) = Unit
						
						override fun onFinish() = hide()
					}
					countDownTimer.start()
				}
			}
			
			override fun hide() {
				viewBinding.apply {
					loInlineMessage.visibility = View.GONE
				}
			}
		}
	}

	@RequiresApi(Build.VERSION_CODES.R)
	private fun initOpsManager(){
		val appOpsCallback =
		object : AppOpsManager.OnOpNotedCallback() {
			override fun onNoted(op: SyncNotedAppOp) {
				logPrivateDataAccess(
					op.op, Throwable().stackTrace.toString()
				)
			}

			override fun onSelfNoted(op: SyncNotedAppOp) {
				logPrivateDataAccess(
					op.op, Throwable().stackTrace.toString()
				)
			}

			override fun onAsyncNoted(asyncOp: AsyncNotedAppOp) {
				logPrivateDataAccess(
					asyncOp.op, Throwable().stackTrace.toString()
				)
			}

			private fun logPrivateDataAccess(opCode: String, trace: String) {
				Log.i(
					"LOG_PRIVATE", "Private data accessed. " +
							"Operation: $opCode\nStack Trace:\n$trace"
				)
			}
		}
		val appOpsManager = getSystemService(AppOpsManager::class.java) as AppOpsManager
		appOpsManager.setOnOpNotedCallback(mainExecutor, appOpsCallback)
	}
	
	override fun onBack(): Boolean {
		val hostId = viewBinding.hostsFragment.id
		val currentFragment = supportFragmentManager.findFragmentById(hostId) as? PVFragment<*>
		return currentFragment?.onBack() ?: false
	}
}