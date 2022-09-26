package com.pvcombank.sdk.view.otp.confirm_otp

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
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
import com.pvcombank.sdk.base.PVFragment
import com.pvcombank.sdk.base.model.TopBarListener
import com.pvcombank.sdk.databinding.OtpViewBinding
import com.pvcombank.sdk.model.CardModel
import com.pvcombank.sdk.model.ErrorBody
import com.pvcombank.sdk.model.MasterModel
import com.pvcombank.sdk.model.request.RequestModel
import com.pvcombank.sdk.model.request.RequestVerifyOTP
import com.pvcombank.sdk.model.response.ResponsePurchase
import com.pvcombank.sdk.model.response.ResponseVerifyOTP
import com.pvcombank.sdk.model.response.ResponseVerifyOnboardOTP
import com.pvcombank.sdk.repository.AuthRepository
import com.pvcombank.sdk.util.security.SecurityHelper
import com.pvcombank.sdk.view.otp.select_card.PaymentInformationFragment
import com.pvcombank.sdk.view.popup.AlertPopup
import com.pvcombank.sdk.view.popup.GuideCardCaptureDialog
import com.pvcombank.sdk.view.register.guide.card.GuideCardIdFragment

class AuthOTPFragment : PVFragment<OtpViewBinding>() {
	private var repository: AuthRepository? = null
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
			topBar.setTitle(getString(R.string.confirm_otp))
			topBar.show()
			topBar.addButtonMore(title = "Huỷ", listener = object : TopBarListener.MoreListener{
				override fun onMoreClick() {
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
			val text = getString(
				R.string.sended_otp_to_number_phone,
				arguments?.getParcelable<ResponsePurchase>("data")?.phoneNumber
			)
			val spanText = SpannableString(text)
			spanText.setSpan(
				ForegroundColorSpan(Color.parseColor("#0072BC")),
				36,
				48,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
			)
			tvShowNumberPhone.text = spanText
			startCountDownTimer()
			repository!!.onNeedLogin.observe(
				viewLifecycleOwner,
				Observer {
					AlertPopup.show(
						title = "Thông báo",
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
	}
	
	private fun getNewOtp() {
		viewBinding.tvGetOtp.isEnabled = false
		viewBinding.tvGetOtp.setTextColor(Color.parseColor("#82869E"))
		val card = requireArguments().getParcelable<CardModel>("card")
		showLoading()
		if (MasterModel.getInstance().isCreateAccount){
			val mail = MasterModel.getInstance().cacheCreateAccountMail
			val phone = MasterModel.getInstance().cacheCreateAccountPhone
			
			repository?.sendOTP(phone, mail){
				if (it !is String){
				
				}
				hideLoading()
				startCountDownTimer()
			}
		} else{
			repository?.purchase(card?.cardToken ?: "") {
				when (it) {
					is ResponsePurchase -> {
						MasterModel.getInstance().uuidOfOTP = it.uuid
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
			uuid = MasterModel.getInstance().uuidOfOTP,
			otp = getOTP()
		)
		
		val stringEncrypt = SecurityHelper.instance()
			.cryptoBuild(type = SecurityHelper.AES)
			?.encrypt(Gson().toJson(request))
		if (MasterModel.getInstance().isCreateAccount){
			repository?.verifyOnboardOTP(RequestModel(data = stringEncrypt)){
				if (it is ResponseVerifyOnboardOTP){
					if (it.pvconnect.detected()){
						when(it.ekyc.ekycStatus){
							//Nhiều case quá chịu
							else -> {
								openFragment(
									GuideCardIdFragment::class.java,
									Bundle(),
									true
								)
							}
						}
					} else {
						//Đăng ký pvconnect
					}
				}
			}
		}else{
			repository?.verifyOTP(RequestModel(data = stringEncrypt)) {
				when (it) {
					is ResponseVerifyOTP -> {
						MasterModel.getInstance().successString.onNext("Thanh toán thành công")
						viewBinding.errorMessage.text = ""
						AlertPopup.show(
							icon = R.drawable.ic_success,
							fragmentManager = childFragmentManager,
							title = "Thanh toán thành công",
							message = "Bạn sẽ được đưa về ${MasterModel.getInstance().clientId} để tiếp tục đơn hàng sau 5 giây.\n",
							primaryTitle = "Quay về ${MasterModel.getInstance().clientId}",
							autoFinish = 6000L,
							primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
								override fun onClickListener(v: View) {
									requireActivity().finish()
								}
							}
						)
					}
					else -> {
						(it as? RequestModel)?.let { errorModel ->
							errorModel.code?.let {
								when (errorModel.code) {
									"117" -> {
										viewBinding.errorMessage.text = errorModel.message
									}
									else -> {
										showAlertErrorPayment(errorModel.message)
									}
								}
							} ?: kotlin.run {
								showAlertErrorPayment(getString(R.string.error_system))
							}
							MasterModel.getInstance().errorString.onNext(errorModel.message ?: getString(R.string.error_system))
						} ?: run {
							showAlertErrorPayment(getString(R.string.error_system))
						}
					}
				}
				hideLoading()
			}
		}
	}
	
	private fun showAlertErrorPayment(message: String? = null) {
		AlertPopup.show(
			icon = R.drawable.warning,
			fragmentManager = childFragmentManager,
			title = message ?: "Thanh toán không thành công",
			primaryTitle = "Thử lại",
			primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
				override fun onClickListener(v: View) {
					openFragment(
						PaymentInformationFragment::class.java,
						Bundle(),
						false
					)
				}
			},
			secondTitle = "Liên hệ Hotline",
			secondButtonListener = object : AlertPopup.SecondButtonListener {
				override fun onClickListener(v: View) {
					Handler(Looper.getMainLooper()).post {
						openFragment(PaymentInformationFragment::class.java, Bundle(), false)
						requireActivity().startActivity(
							Intent(
								Intent.ACTION_DIAL,
								Uri.parse("tel:1900555592")
							)
						)
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
		val time = object : CountDownTimer(120000, 1000) {
			override fun onTick(millisUntilFinished: Long) {
				if (context != null){
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
		time.start()
	}
	
	override fun onBack(): Boolean = false
}