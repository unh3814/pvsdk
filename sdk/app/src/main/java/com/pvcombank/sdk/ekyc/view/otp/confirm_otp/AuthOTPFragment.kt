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
import android.widget.Toast
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.pvcombank.sdk.R
import com.pvcombank.sdk.ekyc.base.PVFragment
import com.pvcombank.sdk.ekyc.base.model.TopBarListener
import com.pvcombank.sdk.databinding.OtpViewBinding
import com.pvcombank.sdk.ekyc.model.CardModel
import com.pvcombank.sdk.ekyc.model.ErrorBody
import com.pvcombank.sdk.ekyc.model.MasterModel
import com.pvcombank.sdk.ekyc.model.request.RequestModel
import com.pvcombank.sdk.ekyc.model.request.RequestVerifyOTP
import com.pvcombank.sdk.ekyc.model.response.ResponsePurchase
import com.pvcombank.sdk.ekyc.model.response.ResponseVerifyOnboardOTP
import com.pvcombank.sdk.ekyc.repository.AuthRepository
import com.pvcombank.sdk.ekyc.util.Utils.phoneHide
import com.pvcombank.sdk.ekyc.util.security.SecurityHelper
import com.pvcombank.sdk.ekyc.view.login.AuthWebLoginFragment
import com.pvcombank.sdk.ekyc.view.popup.AlertPopup
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
			topBar.setTitle("Xác nhận OTP")
			topBar.show()
			topBar.addButtonMore(title = "Huỷ", listener = object : TopBarListener.MoreListener {
				override fun onMoreClick() {
					AlertPopup.show(
						fragmentManager = childFragmentManager,
						message = "Bạn có muốn huỷ giao dịch này không",
						primaryTitle = "OK",
						primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
							override fun onClickListener(v: View) {
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
			repository?.onNeedLogin?.observe(
				viewLifecycleOwner,
				Observer {
					AlertPopup.show(
						message = "Hết thời gian đăng nhập, mời bạn đăng nhập lại",
						primaryTitle = "OK",
						primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
							override fun onClickListener(v: View) {
								requireActivity().recreate()
							}
						},
						fragmentManager = childFragmentManager
					)
				}
			)
			otpFirst.requestFocus()
			showKeyboard(otpFirst)
		}
	}
	
	override fun onStop() {
		super.onStop()
		handler.removeCallbacksAndMessages(null)
		repository?.onNeedLogin?.postValue(null)
		clearOTP()
	}
	
	private fun getNewOtp() {
		viewBinding.tvGetOtp.isEnabled = false
		viewBinding.tvGetOtp.setTextColor(Color.parseColor("#82869E"))
		val card = requireArguments().getParcelable<CardModel>("card")
		showLoading()
		if (masterModel.isCreateAccount) {
			val mail = (cache["email"] as? String) ?: ""
			val phone = (cache["phone_number"] as? String) ?: ""
			
			repository?.sendOTP(phone, mail) {
				if (it !is String) {
				
				}
				hideLoading()
				startCountDownTimer()
			}
		} else {
			repository?.purchase(card?.cardToken ?: "") {
				when (it) {
					is ResponsePurchase -> {
						masterModel.uuidOfOTP = it.uuid
					}
					is ErrorBody -> {
						Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
					}
					is String -> {
						Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
					}
				}
				hideLoading()
				startCountDownTimer()
			}
		}
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
			otp = getOTP()
		)
		
		val stringEncrypt = SecurityHelper.instance()
			.cryptoBuild(type = SecurityHelper.AES)
			?.encrypt(Gson().toJson(request))
		if (masterModel.isCreateAccount) {
			repository?.verifyOnboardOTP(RequestModel(data = stringEncrypt)) {
				hideLoading()
				if (it is ResponseVerifyOnboardOTP) {
					masterModel.ocrFromOTP = it.ekyc
					masterModel.getDataOCR().mobilePhone = (cache["phone_number"] as? String) ?: ""
					when (it.ekyc.step) {
						0, 5, 6 -> {
							AlertPopup.show(
								fragmentManager = childFragmentManager,
								message = "Tài khoản đã được tạo, vui lòng đăng nhập.",
								primaryTitle = "Đồng ý",
								primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
									override fun onClickListener(v: View) {
										openFragment(
											AuthWebLoginFragment::class.java,
											Bundle(),
											false
										)
									}
								}
							)
						}
						1 -> {
							masterModel.timeLogin = Date().time
							openFragment(
								GuideCardIdFragment::class.java,
								Bundle(),
								true
							)
						}
						2 -> {
							masterModel.timeLogin = Date().time
							openFragment(
								GuideFaceIdFragment::class.java,
								Bundle(),
								true
							)
						}
						3, 4 -> {
							masterModel.timeLogin = Date().time
							requireArguments().putBoolean("hide_back", true)
							openFragment(
								InformationConfirmFragment::class.java,
								requireArguments(),
								true
							)
						}
						7 -> {
							AlertPopup.show(
								fragmentManager = childFragmentManager,
								message = "Từ chối",
								primaryTitle = "Đồng ý",
								primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
									override fun onClickListener(v: View) {
									
									}
								}
							)
						}
					}
					timeCountDownTimer?.cancel()
				} else {
					clearOTP()
					(it as? RequestModel)?.let { errorModel ->
						errorModel.code?.let {
							when (errorModel.code) {
								"117" -> {
									viewBinding.errorMessage.text = errorModel.message
								}
								else -> {
									showAlerError(getString(R.string.error_system))
								}
							}
						} ?: kotlin.run {
							showAlerError(getString(R.string.error_system))
						}
						masterModel.errorString.onNext(
							errorModel.message ?: getString(R.string.error_system)
						)
					} ?: run {
						showAlerError(getString(R.string.error_system))
					}
				}
			}
		}
	}
	
	private fun showAlerError(message: String? = null) {
		AlertPopup.show(
			fragmentManager = childFragmentManager,
			message = message ?: "Đã có lỗi, vui lòng thử lại sau.",
			primaryTitle = "Thử lại",
			primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
				override fun onClickListener(v: View) {
					if (masterModel.isCreateAccount) {
						clearOTP()
					}
				}
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
		viewBinding.containerOtp.clearChildFocus(viewBinding.otpFirst)
		viewBinding.containerOtp.forEach {
			(it as? EditText)?.setText("")
		}
	}
	
	override fun onBack(): Boolean = false
}