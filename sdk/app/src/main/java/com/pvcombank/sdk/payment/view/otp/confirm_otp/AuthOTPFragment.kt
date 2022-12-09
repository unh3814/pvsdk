package com.pvcombank.sdk.payment.view.otp.confirm_otp

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
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.pvcombank.sdk.R
import com.pvcombank.sdk.payment.base.PVFragment
import com.pvcombank.sdk.payment.base.model.TopBarListener
import com.pvcombank.sdk.databinding.OtpViewBinding
import com.pvcombank.sdk.payment.model.CardModel
import com.pvcombank.sdk.payment.model.MasterModel
import com.pvcombank.sdk.payment.model.request.RequestVerifyOTP
import com.pvcombank.sdk.payment.model.response.ResponsePurchase
import com.pvcombank.sdk.payment.repository.PVRepository
import com.pvcombank.sdk.payment.util.security.SecurityHelper
import com.pvcombank.sdk.payment.view.otp.select_card.PaymentInformationFragment
import com.pvcombank.sdk.payment.view.popup.AlertPopup

class AuthOTPFragment : PVFragment<OtpViewBinding>() {
	private var repository: PVRepository? = null
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
		repository = PVRepository()
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
			repository!!.observablePurchase.observe(
				viewLifecycleOwner,
				Observer {
					it?.let {
						hideLoading()
						startCountDownTimer()
						MasterModel.getInstance().uuidOfOTP = it.uuid
					}
				}
			)
			repository!!.observableVerify.observe(
				viewLifecycleOwner,
				Observer {
					it?.let {
						hideLoading()
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
				}
			)
			repository!!.error.observe(
				viewLifecycleOwner,
				Observer {
					it?.let {
						hideLoading()
						showAlertErrorPayment(it.second ?: getString(R.string.error_system))
					}
				}
			)
			otpFirst.requestFocus()
			showKeyboard(otpFirst)
		}
	}
	
	override fun onStop() {
		super.onStop()
		repository?.clear()
		handler.removeCallbacksAndMessages(null)
	}
	
	private fun getNewOtp() {
		viewBinding.tvGetOtp.isEnabled = false
		viewBinding.tvGetOtp.setTextColor(Color.parseColor("#82869E"))
		val card = requireArguments().getParcelable<CardModel>("card")
		showLoading()
		repository?.purchase(card!!)
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
		repository?.verifyOTP(
			RequestVerifyOTP(
				uuid = MasterModel.getInstance().uuidOfOTP,
				otp = getOTP()
			)
		)
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