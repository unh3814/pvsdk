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
import com.trustingsocial.tvcoresdk.external.*
import com.trustingsocial.tvsdk.TrustVisionSDK

class PVCBActivity : PVActivity<ActivityPvcbBinding>() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		viewBinding = ActivityPvcbBinding.inflate(layoutInflater)
		setContentView(viewBinding.root)
		initLoading()
		initAlertInline()
		initTopBar()
		viewBinding.apply {
//			openFragment(
//				AuthWebLoginFragment::class.java,
//				Bundle(),
//				false
//			)
			initTrustVision()
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
	
	fun initTrustVision() {
		val configuration = "{\n" +
				"  \"data\": {\n" +
				"    \"card_types\": [\n" +
				"      {\n" +
				"        \"code\": \"vn.national_id\",\n" +
				"        \"name\": \"CMND cũ / CMND mới / CCCD / Hộ chiếu\",\n" +
				"        \"orientation\": \"horizontal\",\n" +
				"        \"has_back_side\": true,\n" +
				"        \"front_qr\": {\n" +
				"          \"exist\": false\n" +
				"        },\n" +
				"        \"back_qr\": {\n" +
				"          \"exist\": false\n" +
				"        }\n" +
				"      }\n" +
				"    ],\n" +
				"    \"country\": \"vn\",\n" +
				"    \"settings\": {\n" +
				"      \"enable_compare_faces\": true,\n" +
				"      \"enable_detect_id_card_tampering\": true,\n" +
				"      \"enable_face_retrieval\": true,\n" +
				"      \"enable_index_faces\": true,\n" +
				"      \"enable_read_id_card_info\": true,\n" +
				"      \"enable_verify_face_liveness\": true,\n" +
				"      \"enable_verify_id_card_sanity\": true,\n" +
				"      \"enable_verify_portrait_sanity\": true,\n" +
				"      \"liveness_modes\": [\n" +
				"        \"active\",\n" +
				"        \"passive\"\n" +
				"      ],\n" +
				"      \"scan_qr\": \"none\",\n" +
				"      \"sdk_settings\": {\n" +
				"        \"active_liveness_settings\": {\n" +
				"          \"face_tracking_setting\": {\n" +
				"            \"android_terminate_threshold\": 0.002847,\n" +
				"            \"android_warning_threshold\": 0.001474,\n" +
				"            \"enable\": true,\n" +
				"            \"ios_terminate_threshold\": 0.003393,\n" +
				"            \"ios_warning_threshold\": 0.002176,\n" +
				"            \"limit_for\": \"all_flow\",\n" +
				"            \"max_interval_ms\": 2000,\n" +
				"            \"max_warning_time\": 5,\n" +
				"            \"web_terminate_threshold\": 0.0030152991993743408,\n" +
				"            \"web_warning_threshold\": 0.0017317430600108828\n" +
				"          },\n" +
				"          \"flow_interval_time_ms\": 3000,\n" +
				"          \"limit_time_liveness_check\": {\n" +
				"            \"enable\": true,\n" +
				"            \"limit_time_second\": 45\n" +
				"          },\n" +
				"          \"record_video\": {\n" +
				"            \"enable\": false\n" +
				"          },\n" +
				"          \"save_encoded_frames\": {\n" +
				"            \"enable\": true,\n" +
				"            \"frames_interval_ms\": 180\n" +
				"          },\n" +
				"          \"show_gesture_arrow\": true,\n" +
				"          \"terminate_if_no_face\": {\n" +
				"            \"enable\": true,\n" +
				"            \"max_invalid_frame\": 5,\n" +
				"            \"max_time_ms\": 1000\n" +
				"          }\n" +
				"        },\n" +
				"        \"id_detection_settings\": {\n" +
				"          \"auto_capture\": {\n" +
				"            \"enable\": false,\n" +
				"            \"show_capture_button\": true\n" +
				"          },\n" +
				"          \"blur_check\": {\n" +
				"            \"enable\": true,\n" +
				"            \"threshold\": 0.29\n" +
				"          },\n" +
				"          \"disable_capture_button_if_alert\": true,\n" +
				"          \"glare_check\": {\n" +
				"            \"enable\": true,\n" +
				"            \"threshold\": 0.001\n" +
				"          },\n" +
				"          \"id_detection\": {\n" +
				"            \"enable\": true\n" +
				"          },\n" +
				"          \"save_frame_settings\": {\n" +
				"            \"enable\": false,\n" +
				"            \"frames_interval_ms\": 190,\n" +
				"            \"quality_android\": 80,\n" +
				"            \"quality_ios\": 70,\n" +
				"            \"quality_web\": 80\n" +
				"          },\n" +
				"          \"scan_qr_settings\": {\n" +
				"            \"enable\": false,\n" +
				"            \"limit_time_second\": 45\n" +
				"          }\n" +
				"        }\n" +
				"      },\n" +
				"      \"selfie_camera_options\": [\n" +
				"        \"front\"\n" +
				"      ],\n" +
				"      \"selfie_enable_detect_multiple_face\": true,\n" +
				"      \"support_transaction\": false,\n" +
				"      \"web_app_crop_face\": \"auto\"\n" +
				"    }\n" +
				"  }\n" +
				"}"
		TrustVisionSDK.init(
			configuration,
			"vi",
			null
		)
		val configurationCardID = TVIDConfiguration.Builder()
			.setCardType(TrustVisionSDK.getCardTypes().first())
			.setCardSide(TVSDKConfiguration.TVCardSide.FRONT)
			.setReadBothSide(true)
			.setEnableSound(false)
			.setEnablePhotoGalleryPicker(false)
			.setEnableTiltChecking(false)
			.setSkipConfirmScreen(false)
		val mConfiguration = configurationCardID.build()
		TrustVisionSDK.startIDCapturing(this, mConfiguration, object : TVCapturingCallBack() {
			override fun onNewFrameBatch(p0: FrameBatch) {
				println("New frame ${p0}")
			}
			
			override fun onError(p0: TVDetectionError?) {
				println("Error ${p0}")
			}
			
			override fun onSuccess(p0: TVDetectionResult?) {
				println("Success ${p0}")
			}
			
			override fun onCanceled() {
				println("Cancel")
			}
			
		})
	}
}