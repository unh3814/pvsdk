package com.pvcombank.sdk.payment.view.otp.confirm_card

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.pvcombank.sdk.R
import com.pvcombank.sdk.payment.base.PVFragment
import com.pvcombank.sdk.databinding.FragmentConfirmPaymentBinding
import com.pvcombank.sdk.payment.model.CardModel
import com.pvcombank.sdk.payment.model.Constants
import com.pvcombank.sdk.payment.model.MasterModel
import com.pvcombank.sdk.payment.model.response.ResponsePurchase
import com.pvcombank.sdk.payment.repository.AuthRepository
import com.pvcombank.sdk.payment.util.Utils.formatStringCurrency
import com.pvcombank.sdk.payment.view.login.AuthWebLoginFragment
import com.pvcombank.sdk.payment.view.otp.confirm_otp.AuthOTPFragment
import com.pvcombank.sdk.payment.view.popup.AlertPopup

class PaymentConfirmInformationFragment : PVFragment<FragmentConfirmPaymentBinding>() {
	private var cardSelected: CardModel? = null
	private val repository = AuthRepository()
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		viewBinding = FragmentConfirmPaymentBinding.inflate(inflater, container, false)
		return viewBinding.root
	}
	
	@SuppressLint("SetTextI18n")
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewBinding.apply {
			hideInlineMessage()
			topBar.setTitle(getString(R.string.confirm_information_payment))
			topBar.show()
			cardSelected = arguments?.getParcelable<CardModel>("card")?.apply {
				tvLabelCard.text = cardType
				tvNumberCard.text = numberCard
				tvCardBalance.text = getString(
					R.string.current_balance,
					availableBalance.toString().formatStringCurrency()
				)
			}
			MasterModel.getInstance().apply {
				tvNumberOrder.text = idOrder
				tvCurrentBalance.text = "${orderCurrency?.formatStringCurrency()}đ"
				tvContentOrder.text = getString(R.string.content_order_desc, idOrder, clientId)
				//show alert if error card not enough money
				val cardSelectedCurrency = cardSelected?.availableBalance?.toString() ?: "0"
				val currencyOrder = orderCurrency ?: "0"
				if (cardSelectedCurrency.toBigDecimal()
						.minus(currencyOrder.toBigDecimal()) < "0".toBigDecimal()
				) {
					tvAlertCard.visibility = View.VISIBLE
					btnConfirm.isEnabled = false
				}
			}
			btnConfirm.setOnClickListener {
				cardSelected?.cardToken?.let {
					checkStatusCard(it)
				}
			}
			btnCancelOrder.setOnClickListener {
				AlertPopup.show(
					fragmentManager = childFragmentManager,
					title = "Thông báo",
					message = "Bạn có muốn huỷ giao dịch này không",
					primaryTitle = "OK",
					primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
						override fun onClickListener(v: View) {
							MasterModel.getInstance().errorString.onNext("Đơn hàng được huỷ theo yêu cầu")
							requireActivity().finish()
						}
					},
					secondTitle = "Cancel",
					secondButtonListener = object : AlertPopup.SecondButtonListener {
						override fun onClickListener(v: View) = Unit
					}
				)
			}
			repository.onNeedLogin.observe(
				viewLifecycleOwner,
				Observer {
					AlertPopup.show(
						title = "Thông báo",
						message = "Hết thời gian đăng nhập, mời bạn đăng nhập lại",
						primaryTitle = "OK",
						primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
							override fun onClickListener(v: View) {
								openFragment(
									AuthWebLoginFragment::class.java,
									Bundle(),
									false
								)
							}
						},
						fragmentManager = childFragmentManager
					)
				}
			)
			
		}
	}
	
	private fun checkStatusCard(it: String) {
		showLoading()
		repository.getCard(it) {
			when (it) {
				is CardModel -> {
					Constants.INLINE_ALERT_CODE[it.cardStatus.toInt()]?.let { message ->
						hideLoading()
						showInlineMessage(message = message)
					}
					Constants.TOAST_ALERT_CODE[it.cardStatus.toInt()]?.let { message ->
						hideLoading()
						showToastMessage(message)
					}
					if(it.cardStatus.toInt() == 0){
						startPurchase()
					}
				}
				else -> {
					hideLoading()
					showAlertError()
				}
			}
		}
	}
	
	private fun showAlertError(message: String? = null) {
		AlertPopup.show(
			fragmentManager = childFragmentManager,
			title = "Thông báo",
			primaryTitle = "ok",
			message = message ?: getString(R.string.error_system)
		)
	}
	
	private fun startPurchase() {
		showLoading()
		repository.purchase(cardSelected?.cardToken ?: "") {
			hideLoading()
			when (it) {
				is ResponsePurchase -> {
					MasterModel.getInstance().uuidOfOTP = it.uuid
					requireArguments().apply {
						putParcelable("data", it)
					}
					openFragment(
						AuthOTPFragment::class.java,
						requireArguments(),
						true
					)
				}
				else -> {
					MasterModel.getInstance().errorString.onNext("Lỗi hệ thống")
					AlertPopup.show(
						fragmentManager = childFragmentManager,
						title = "Thông báo",
						message = "Đã có lỗi xảy ra",
						primaryTitle = "OK"
					)
				}
			}
		}
	}
	
	override fun onBack(): Boolean {
		return false
	}
}