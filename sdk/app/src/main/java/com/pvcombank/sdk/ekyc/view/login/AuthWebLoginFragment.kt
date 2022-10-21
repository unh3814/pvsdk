package com.pvcombank.sdk.ekyc.view.login

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import com.pvcombank.sdk.ekyc.base.PVFragment
import com.pvcombank.sdk.databinding.FragmentWebLoginBinding
import com.pvcombank.sdk.ekyc.model.Constants
import com.pvcombank.sdk.ekyc.model.MasterModel
import com.pvcombank.sdk.ekyc.repository.AuthRepository
import com.pvcombank.sdk.ekyc.view.popup.AlertPopup
import com.pvcombank.sdk.ekyc.view.register.after_create.AfterCreateFragment
import com.pvcombank.sdk.ekyc.view.register.guide.card.GuideCardIdFragment
import java.util.*

class AuthWebLoginFragment : PVFragment<FragmentWebLoginBinding>() {
	private val webClient = object : WebViewClient() {
		override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
			handlerUrl(url)
		}
		
		override fun onPageFinished(view: WebView?, url: String?) {
			super.onPageFinished(view, url)
		}
		
		override fun onReceivedError(
			view: WebView?,
			request: WebResourceRequest?,
			error: WebResourceError?
		) {
			hideLoading()
//			viewBinding.layoutLoaddingWeb.visibility = View.VISIBLE
			if (error?.errorCode == -2) {
				AlertPopup.show(
					fragmentManager = childFragmentManager,
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
			if (url?.startsWith(Constants.REDIRECT_SANBOX_URL) == true){
				Log.d("success", "Login success")
			}
		}
	}
	private val chromeClient = object : WebChromeClient() {
		override fun onProgressChanged(view: WebView?, newProgress: Int) {
			super.onProgressChanged(view, newProgress)
			if (newProgress < 100 && view?.url?.startsWith(Constants.REDIRECT_SANBOX_URL) == false){
				showLoading()
			} else {
				hideLoading()
			}
//			if (view?.url?.startsWith(Constants.REDIRECT_SANBOX_URL) == true && view?.url != Constants.url) {
//				viewBinding.layoutLoaddingWeb.visibility = View.VISIBLE
//			}
//			if (view?.url == Constants.url) {
//				viewBinding.layoutLoaddingWeb.visibility = View.GONE
//			}
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
		MasterModel.getInstance().cleanOCR()
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
			loadUrl(Constants.url_sanbox)
		}
	}
	
	private fun handlerUrl(url: String?) {
		val uri = Uri.parse(url)
		toCreateUser(uri)
		toLogin(url)
	}
	
	private fun toCreateUser(uri: Uri) {
		if (uri.pathSegments.last() == "registration") {
			openFragment(
				AfterCreateFragment::class.java,
				Bundle(),
				true
			)
		}
	}
	
	private fun toLogin(url: String?) {
		if (url?.startsWith(Constants.REDIRECT_SANBOX_URL) == true) {
			showLoading()
			Uri.parse(url)?.apply {
				this.getQueryParameter("code")?.let {
					val code = it
					AuthRepository().apply {
						Handler(Looper.getMainLooper()).post {
							getTokenByCode(
								code = code,
								clientId = Constants.CLIENT_ID,
								clientSecret = Constants.CLIENT_SECRET
							) {
								hideLoading()
								Constants.TOKEN = "${it?.tokenType ?: ""} ${it?.accessToken ?: ""}"
								openFragment(
									GuideCardIdFragment::class.java,
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
			message = "Bạn có muốn kết thúc không",
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