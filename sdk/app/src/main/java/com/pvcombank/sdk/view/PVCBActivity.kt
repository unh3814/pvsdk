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
	private var trustVisionConfiguration = "";
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		viewBinding = ActivityPvcbBinding.inflate(layoutInflater)
		setContentView(viewBinding.root)
		initLoading()
		initAlertInline()
		initTopBar()
		//init TrustVisionSDK
//		Handler(Looper.getMainLooper()).post {
//			TrustVisionSDK.init(
//				trustVisionConfiguration,
//				"vi",
//				null
//			)
//		}
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
		supportFragmentManager.addFragmentOnAttachListener { fragmentManager, fragment ->
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