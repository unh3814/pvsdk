package com.pvcombank.sdk.ekyc.view.otp.confirm_otp

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.pvcombank.sdk.ekyc.R
import com.pvcombank.sdk.ekyc.base.PVFragment
import com.pvcombank.sdk.ekyc.base.model.TopBarListener
import com.pvcombank.sdk.ekyc.databinding.OtpViewBinding
import com.pvcombank.sdk.ekyc.model.CardModel
import com.pvcombank.sdk.ekyc.model.Constants
import com.pvcombank.sdk.ekyc.model.MarcomEvent
import com.pvcombank.sdk.ekyc.model.MasterModel
import com.pvcombank.sdk.ekyc.model.request.RequestModel
import com.pvcombank.sdk.ekyc.model.request.RequestVerifyOTP
import com.pvcombank.sdk.ekyc.model.response.ResponsePurchase
import com.pvcombank.sdk.ekyc.model.response.ResponseVerifyOnboardOTP
import com.pvcombank.sdk.ekyc.repository.AuthRepository
import com.pvcombank.sdk.ekyc.util.Utils.phoneHide
import com.pvcombank.sdk.ekyc.util.Utils.timeToString
import com.pvcombank.sdk.ekyc.util.security.SecurityHelper
import com.pvcombank.sdk.ekyc.view.popup.AlertPopup
import com.pvcombank.sdk.ekyc.view.register.after_create.AfterCreateFragment
import com.pvcombank.sdk.ekyc.view.register.confirm.InformationConfirmFragment
import com.pvcombank.sdk.ekyc.view.register.guide.card.GuideCardIdFragment
import com.pvcombank.sdk.ekyc.view.register.guide.face.GuideFaceIdFragment
import java.util.*

class AuthOTPFragment : PVFragment<OtpViewBinding>() {
	private var repository: AuthRepository? = null
	private val masterModel get() = MasterModel.getInstance()
	private val cache get() = masterModel.cache
	private var timeCountDownTimer: CountDownTimer? = null
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		viewBinding = OtpViewBinding.inflate(inflater, container, false)
		return viewBinding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		repository = AuthRepository()
		viewBinding.apply {
			topBar.setTitle("Xác thực OTP")
			topBar.show()
			topBar.addButtonMore(title = "Huỷ", listener = object : TopBarListener.MoreListener {
				override fun onMoreClick() {
					AlertPopup.show(
						fragmentManager = childFragmentManager,
						message = "Bạn có muốn huỷ giao dịch này không",
						primaryTitle = "OK",
						primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
							override fun onClickListener(v: View) {
								MasterModel.getInstance().errorString.onNext("Cancel")
								requireActivity().finish()
							}
						},
						secondTitle = "Cancel"
					)
				}
			})
			tvGetOtp.setOnClickListener {
				getNewOtp()
			}
			containerOtp.forEachIndexed { _, view ->
				(view as? EditText)?.let {
					it.validateOtpView()
					it.setOnKeyListener { v, keyCode, event ->
						if (
							event.action == KeyEvent.ACTION_DOWN
							&& keyCode == KeyEvent.KEYCODE_DEL
							&& v.id != otpFirst.id
							&& (v as? EditText)?.text.isNullOrEmpty()
						) {
							behindFocus(v)
							true
						}
						if (event.action == KeyEvent.ACTION_DOWN
							&& keyCode != KeyEvent.KEYCODE_DEL
							&& v.id != otpSix.id
							&& !(v as? EditText)?.text.isNullOrEmpty()
						) {
							nextFocus(v, event.unicodeChar.toChar().toString())
							true
						}
						false
					}
					view.setOnFocusChangeListener { v, hasFocus ->
						if (hasFocus) {
							(v as? EditText)?.let { et ->
								et.setSelection(et.text.length)
							}
						}
					}
				}
			}
			val phoneStr = requireArguments().getParcelable<ResponsePurchase>("data")?.phoneNumber
				?: (cache["phone_number"] as? String) ?: ""
			
			val text = getString(
				R.string.sended_otp_to_number_phone,
				phoneStr.phoneHide()
			)
			val spanText = SpannableString(text)
			spanText.setSpan(
				ForegroundColorSpan(Color.parseColor("#0072BC")),
				text.length - 10,
				text.length,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
			)
			tvShowNumberPhone.text = spanText
			startCountDownTimer()
			otpFirst.requestFocus()
			showKeyboard(otpFirst)
		}
		repository?.observerVerifyOTP
			?.observe(
				viewLifecycleOwner,
				Observer {
					it?.let {
						hideLoading()
						updateTokenAndDirectorScreen(it)
						timeCountDownTimer?.cancel()
					}
				}
			)
		repository?.observerSendOTPResponse?.observe(
			viewLifecycleOwner,
			Observer {
				hideLoading()
				it?.let {
					MasterModel.getInstance().uuidOfOTP = it.uuid
					logEvent(
						this@AuthOTPFragment::class.java.simpleName,
						MarcomEvent.OPEN_OTP_FORM,
						mutableMapOf(
							Pair("af_sent_otp_status", true),
							Pair("af_sent_otp_time", Date().time.timeToString(Constants.MARCOM_DATE_TIME))
						)
					)
				}
			}
		)
		repository?.error?.observe(
			viewLifecycleOwner,
			Observer {
				it?.let {
					clearOTP()
					logEvent(
						this@AuthOTPFragment::class.java.simpleName,
						MarcomEvent.OTP_NEXT_FORM,
						mutableMapOf(
							Pair("af_verify_otp_status", "fail"),
							Pair("af_phone", (cache["phone_number"] as? String) ?: "")
						)
					)
					hideLoading()
					if (it.first == 117) {
						showAlerError(it.second) {
						}
					} else {
						showAlerError(it.second) {
							goBack()
						}
					}
				}
			}
		)
	}
	
	private fun updateTokenAndDirectorScreen(it: ResponseVerifyOnboardOTP) {
		Constants.TOKEN = "Bearer ${it.token}"
		masterModel.ocrFromOTP = it.ekyc
		masterModel.getDataOCR().mobilePhone = (cache["phone_number"] as? String) ?: ""
		logEvent(
			this@AuthOTPFragment::class.java.simpleName,
			MarcomEvent.OTP_NEXT_FORM,
			mutableMapOf(
				Pair("af_verify_otp_status", "success"),
				Pair("af_phone", (cache["phone_number"] as? String) ?: "")
			)
		)
		when (it.ekyc.step) {
			0 -> {
				AlertPopup.show(
					fragmentManager = childFragmentManager,
					message = getString(R.string.error_0),
					primaryTitle = getString(R.string.txt_close),
					primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
						override fun onClickListener(v: View) {
							openFragment(
								AfterCreateFragment::class.java,
								Bundle()
							)
						}
					}
				)
			}
			5, 6 -> {
				AlertPopup.show(
					fragmentManager = childFragmentManager,
					message = getString(R.string.error_5),
					primaryTitle = getString(R.string.txt_close),
					primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
						override fun onClickListener(v: View) {
							openFragment(
								AfterCreateFragment::class.java,
								Bundle()
							)
						}
					}
				)
			}
			1 -> {
				masterModel.timeLogin = Date().time
				checkAccessToken(it){
					openFragment(
						GuideCardIdFragment::class.java,
						Bundle()
					)
				}
			}
			2 -> {
				masterModel.timeLogin = Date().time
				checkAccessToken(it){
					openFragment(
						GuideFaceIdFragment::class.java,
						Bundle()
					)
				}
			}
			3, 4 -> {
				masterModel.timeLogin = Date().time
				checkAccessToken(it){
					openFragment(
						InformationConfirmFragment::class.java,
						requireArguments()
					)
				}
			}
			7 -> {
				AlertPopup.show(
					fragmentManager = childFragmentManager,
					message = getString(R.string.error_7),
					primaryTitle = getString(R.string.txt_close),
					primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
						override fun onClickListener(v: View) {
						
						}
					}
				)
			}
		}
	}

	private fun checkAccessToken(responseOtp: ResponseVerifyOnboardOTP, onHadToken: () -> Unit){
		if (responseOtp.token.isNullOrEmpty()){
			AlertPopup.show(
				fragmentManager = childFragmentManager,
				message = "Mã định danh không tồn tại.",
				primaryTitle = getString(R.string.txt_close),
				primaryButtonListener = object : AlertPopup.PrimaryButtonListener{
					override fun onClickListener(v: View) {
						openFragment(
							AfterCreateFragment::class.java,
							Bundle()
						)
					}
				}
			)
		} else onHadToken.invoke()
	}

	override fun onStop() {
		super.onStop()
		handler.removeCallbacksAndMessages(null)
		repository?.clear()
		clearOTP()
	}
	
	private fun getNewOtp() {
		viewBinding.tvGetOtp.isEnabled = false
		viewBinding.tvGetOtp.setTextColor(Color.parseColor("#82869E"))
		showLoading()
		val mail = (cache["email"] as? String) ?: ""
		val phone = (cache["phone_number"] as? String) ?: ""
		repository?.sendOTP(phone, mail)
	}
	
	private fun EditText.validateOtpView() {
		val watcher = object : TextWatcher {
			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
				if (start > 0) {
					(viewBinding.getNextView(this@validateOtpView) as? EditText)?.setText(s.toString())
				}
				if (start == 0 && after > 0) viewBinding.nextFocus(this@validateOtpView)
				if (count > 0 && after == 0) viewBinding.behindFocus(this@validateOtpView)
			}
			
			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
				if (start >= 1) {
				
				}
			}
			
			override fun afterTextChanged(s: Editable?) {
				if (!s.isNullOrEmpty() && getOTP().length == 6) startVerifyOTP()
			}
		}
		this.addTextChangedListener(watcher)
	}
	
	private fun startVerifyOTP() {
		showLoading()
		hideKeyboard()
		val request = RequestVerifyOTP(
			uuid = masterModel.uuidOfOTP,
			otp = getOTP(),
			userIdPartner = Constants.ID_PARTNER
		)
		
		val stringEncrypt = SecurityHelper.instance()
			.cryptoBuild(type = SecurityHelper.AES)
			?.encrypt(Gson().toJson(request))
		repository?.verifyOnboardOTP(RequestModel(data = stringEncrypt))
	}
	
	private fun showAlerError(message: String? = null, onAlertClick: (v: View) -> Unit) {
		AlertPopup.show(
			fragmentManager = childFragmentManager,
			message = message ?: "Đã có lỗi, vui lòng thử lại sau.",
			primaryTitle = getString(R.string.txt_close),
			primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
				override fun onClickListener(v: View) = onAlertClick(v)
			}
		)
	}
	
	private fun OtpViewBinding.behindFocus(currentView: View) {
		(getBehindView(currentView) as? EditText)?.let {
			currentView.clearFocus()
			it.requestFocus()
		}
		
	}
	
	fun OtpViewBinding.getBehindView(currentView: View): View? {
		val currentIndex = getIndex(currentView)
		if (currentIndex >= 0 && currentIndex < (containerOtp.childCount)) {
			return containerOtp.getChildAt(currentIndex - 1)
		}
		return null
	}
	
	private fun OtpViewBinding.nextFocus(currentView: View, value: String? = null) {
		(getNextView(currentView) as? EditText)?.let { et ->
			currentView.clearFocus()
			et.requestFocus()
			value?.let {
				et.setText(it)
			}
		}
	}
	
	fun OtpViewBinding.getNextView(currentView: View): View? {
		val currentIndex = getIndex(currentView)
		if (currentIndex >= 0 && currentIndex < (containerOtp.childCount - 1)) {
			return containerOtp.getChildAt(currentIndex + 1)
		}
		return null
	}
	
	private fun OtpViewBinding.getIndex(v: View): Int {
		containerOtp.forEachIndexed { index, view ->
			if (view.id == v.id) {
				return index
			}
		}
		return 0
	}
	
	private fun getOTP(): String {
		val otpString = StringBuilder()
		viewBinding.containerOtp.forEach {
			otpString.append((it as EditText).text.toString())
		}
		return otpString.toString()
	}
	
	private fun startCountDownTimer() {
		timeCountDownTimer = object : CountDownTimer(120000, 1000) {
			override fun onTick(millisUntilFinished: Long) {
				if (context != null) {
					viewBinding.apply {
						tvGetOtp.isEnabled = false
						tvGetOtp.setTextColor(Color.parseColor("#82869E"))
						tvGetOtp.text = getString(
							R.string.resend_otp,
							(millisUntilFinished / 1000).toString()
						)
					}
				}
			}
			
			override fun onFinish() {
				viewBinding.tvGetOtp.isEnabled = true
				viewBinding.tvGetOtp.text = "Gửi lại mã"
				viewBinding.tvGetOtp.setTextColor(Color.parseColor("#3A9EFC"))
				viewBinding.tvGetOtp.isEnabled = true
			}
		}
		timeCountDownTimer?.start()
	}
	
	private fun clearOTP() {
		viewBinding.containerOtp.forEach {
			(it as? EditText)?.setText("")
		}
		requireActivity().currentFocus?.clearFocus()
	}
	
	override fun onBack(): Boolean = false
}