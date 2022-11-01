package com.pvcombank.sdk.payment.view.login

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import com.pvcombank.sdk.payment.base.PVFragment
import com.pvcombank.sdk.databinding.FragmentWebLoginBinding
import com.pvcombank.sdk.payment.model.Constants
import com.pvcombank.sdk.payment.model.MasterModel
import com.pvcombank.sdk.payment.repository.AuthRepository
import com.pvcombank.sdk.payment.view.otp.select_card.PaymentInformationFragment
import com.pvcombank.sdk.payment.view.popup.AlertPopup
import java.util.*

class AuthWebLoginFragment : PVFragment<FragmentWebLoginBinding>() {
	private val masterData = MasterModel.getInstance()
	private val webClient = object : WebViewClient() {
		override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
			handlerUrl(url)
		}
		
		override fun onPageFinished(view: WebView?, url: String?) {
			super.onPageFinished(view, url)
			hideLoading()
		}
		
		override fun onReceivedError(
			view: WebView?,
			request: WebResourceRequest?,
			error: WebResourceError?
		) {
			hideLoading()
			viewBinding.layoutLoaddingWeb.visibility = View.VISIBLE
			if (error?.errorCode == -2){
				AlertPopup.show(
					fragmentManager = childFragmentManager,
					title = "Thông báo",
					message = "Có lỗi trong quá trình kết nối, vui lòng thử lại.",
					primaryTitle = "OK",
					primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
						override fun onClickListener(v: View) {
							requireActivity().finish()
						}
					}
				)
			}
		}
		
		override fun onLoadResource(view: WebView?, url: String?) {
			super.onLoadResource(view, url)
		}
	}
	private val chromeClient = object : WebChromeClient() {
		override fun onProgressChanged(view: WebView?, newProgress: Int) {
			super.onProgressChanged(view, newProgress)
			if (newProgress < 100){
				showLoading()
				hideKeyboard()
			} else {
				hideLoading()
			}
			if (view?.url?.startsWith(Constants.REDIRECT_URL) == true && view?.url != Constants.url){
				viewBinding.layoutLoaddingWeb.visibility = View.VISIBLE
			}
			if(view?.url == Constants.url){
				viewBinding.layoutLoaddingWeb.visibility = View.GONE
			}
		}
	}
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		viewBinding = FragmentWebLoginBinding.inflate(inflater, container, false)
		return viewBinding.root
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		hideInlineMessage()
		topBar.hide()
		viewBinding.webViewer.apply {
			clearCache(true)
			settings.javaScriptEnabled = true
			settings.builtInZoomControls = true
			settings.loadWithOverviewMode = true
			settings.useWideViewPort = true
//			settings.cacheMode = WebSettings.LOAD_NO_CACHE
//			settings.saveFormData = false
			CookieManager.getInstance().removeAllCookies(null)
			CookieManager.getInstance().flush()
			webViewClient = webClient
			webChromeClient = chromeClient
			loadUrl(Constants.url)
		}
	}
	
	private fun handlerUrl(url: String?) {
		toLogin(url)
	}
	
	private fun toLogin(url: String?) {
		if (url?.startsWith(Constants.REDIRECT_URL) == true) {
			Uri.parse(url)?.apply {
				this.getQueryParameter("code")?.let {
					val code = it
					MasterModel.getInstance().timeLogin = Date().time
					AuthRepository().apply {
						Handler(Looper.getMainLooper()).post {
							getTokenByCode(
								code = code,
								clientId = masterData.clientId ?: "",
								clientSecret = masterData.clientSecret ?: ""
							) {
								Constants.TOKEN = "${it?.tokenType ?: ""} ${it?.accessToken ?: ""}"
								openFragment(
									PaymentInformationFragment::class.java,
									Bundle(),
									true
								)
							}
						}
					}
				}
			}
		}
	}
	
	
	override fun onBack(): Boolean {
		AlertPopup.show(
			fragmentManager = childFragmentManager,
			title = "Thông báo",
			message = "Bạn có muốn huỷ giao dịch này không",
			primaryTitle = "OK",
			primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
				override fun onClickListener(v: View) {
					requireActivity().finish()
				}
			},
			secondTitle = "Cancel",
			secondButtonListener = object : AlertPopup.SecondButtonListener {
				override fun onClickListener(v: View) = Unit
			}
		)
		return true
	}
}