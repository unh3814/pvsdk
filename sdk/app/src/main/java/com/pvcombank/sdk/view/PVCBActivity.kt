package com.pvcombank.sdk.view

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
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

class PVCBActivity : PVActivity<ActivityPvcbBinding>() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		viewBinding = ActivityPvcbBinding.inflate(layoutInflater)
		setContentView(viewBinding.root)
		initLoading()
		initAlertInline()
		initTopBar()
		viewBinding.apply {
			openFragment(
				AuthWebLoginFragment::class.java,
				Bundle(),
				false
			)
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
	
	override fun onBack(): Boolean {
		val hostId = viewBinding.hostsFragment.id
		val currentFragment = supportFragmentManager.findFragmentById(hostId) as? PVFragment<*>
		return currentFragment?.onBack() ?: false
	}
}