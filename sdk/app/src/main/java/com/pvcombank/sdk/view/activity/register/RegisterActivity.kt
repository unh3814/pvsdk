package com.pvcombank.sdk.view.activity.register

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.pvcombank.sdk.R
import com.pvcombank.sdk.base.PVActivity
import com.pvcombank.sdk.base.PVFragment
import com.pvcombank.sdk.base.model.AlertInline
import com.pvcombank.sdk.base.model.TopBar
import com.pvcombank.sdk.base.model.TopBarListener
import com.pvcombank.sdk.databinding.ActivityRegisterBinding
import com.pvcombank.sdk.model.Constants
import com.pvcombank.sdk.util.Utils.openFragment
import com.pvcombank.sdk.view.register.confirm.InformationConfirmFragment
import com.pvcombank.sdk.view.register.home.HomeFragment
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
		viewBinding.apply {
			supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
			openFragment(
				HomeFragment::class.java,
				Bundle(),
				true
			)
		}
		supportFragmentManager.addOnBackStackChangedListener {
			if (currentFragment is InformationConfirmFragment){
				topBar.hideButtonBack()
			} else {
				topBar.showButtonBack()
			}
		}
		supportFragmentManager.addFragmentOnAttachListener { fragmentManager, fragment ->
			if (fragment is InformationConfirmFragment){
				topBar.hideButtonBack()
			} else {
				topBar.showButtonBack()
			}
		}
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
}