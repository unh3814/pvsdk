package com.pvcombank.sdk.ekyc.view

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.pvcombank.sdk.ekyc.R
import com.pvcombank.sdk.ekyc.base.PVActivity
import com.pvcombank.sdk.ekyc.base.PVFragment
import com.pvcombank.sdk.ekyc.base.model.AlertInline
import com.pvcombank.sdk.ekyc.base.model.TopBar
import com.pvcombank.sdk.ekyc.base.model.TopBarListener
import com.pvcombank.sdk.ekyc.databinding.ActivityRegisterBinding
import com.pvcombank.sdk.ekyc.model.Constants
import com.pvcombank.sdk.ekyc.util.Utils.openFragment
import com.pvcombank.sdk.ekyc.view.register.after_create.AfterCreateFragment
import com.trustingsocial.tvsdk.TrustVisionSDK

class RegisterActivity : PVActivity<ActivityRegisterBinding>() {
	private val currentFragment get() = supportFragmentManager.findFragmentById(fragmentHostID)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		viewBinding = ActivityRegisterBinding.inflate(layoutInflater)
		setContentView(viewBinding.root)
		fragmentHostID = viewBinding.hostsFragmentRegister.id
		initLoading(viewBinding.loading)
		initAlertInline()
		initTopBar()
		initTrustVision()
		initAppsflyer()
		viewBinding.apply {
			supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
			openFragment(
				AfterCreateFragment::class.java,
				Bundle()
			)
		}
	}

	private fun initAppsflyer() {
		val appsflyerInitListener = object : AppsFlyerConversionListener {
			override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
				Log.d("AppsFlyer", "onConversionDataSuccess $p0")
			}

			override fun onConversionDataFail(p0: String?) {
				Log.d("AppsFlyer", "$p0")
			}

			override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
				Log.d("AppsFlyer", "onConversionDataFail $p0")
			}

			override fun onAttributionFailure(p0: String?) {
				Log.d("AppsFlyer", "onAppOpenAttribution $p0")
			}
		}
		val appsFlyerStartListener = object : AppsFlyerRequestListener{
			override fun onSuccess() {
				Log.d("AppsFlyer", "Start success")
			}

			override fun onError(p0: Int, p1: String) {
				Log.d("AppsFlyer", "Error: [$p0] - $p1")
			}
		}
		AppsFlyerLib.getInstance().init(
			getString(R.string.key_appflyer),
			appsflyerInitListener,
			this
		)
		AppsFlyerLib.getInstance().start(this, getString(R.string.key_appflyer), appsFlyerStartListener)
	}

	private fun initTopBar() {
		topBar = TopBar.build()
		topBar.setContentView(viewBinding.topBar)
		topBar.setColor(R.color.color_primary)
		topBar.apply {
			addBackListener(object : TopBarListener.BackListener {
				override fun onBackClick() {
					goBack()
				}
			})
			addButtonMore(listener = object : TopBarListener.MoreListener {
				override fun onMoreClick() {
					//Do nothing
				}
			})
			show()
		}
	}
	
	private fun initAlertInline() {
		alertInline = object : AlertInline {
			override fun show(icon: Drawable?, message: String) {
				viewBinding.apply {
					loInlineMessage.visibility = View.VISIBLE
					tvInlineMessage.text = message
					ivIconInlineMessage.setImageDrawable(
						icon ?: ContextCompat.getDrawable(
							this@RegisterActivity,
							R.drawable.ic_untrushed
						)
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
	
	private fun initTrustVision() {
		TrustVisionSDK.init(
			Constants.TS_CONFIGURATION,
			"vi"
		) { event ->
			Log.d("TVSDK Event", "$event")
		}
	}
	
	override fun onBack(): Boolean {
		val currentFragment = supportFragmentManager.findFragmentById(fragmentHostID) as? PVFragment<*>
		return currentFragment?.onBack() ?: false
	}

	override fun onDestroy() {
		super.onDestroy()
	}
}