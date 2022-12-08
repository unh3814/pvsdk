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
import com.pvcombank.sdk.payment.model.MasterModel
import com.pvcombank.sdk.payment.repository.PVRepository
import com.pvcombank.sdk.payment.util.Utils.formatStringCurrency
import com.pvcombank.sdk.payment.view.login.AuthWebLoginFragment
import com.pvcombank.sdk.payment.view.otp.confirm_otp.AuthOTPFragment
import com.pvcombank.sdk.payment.view.popup.AlertPopup

class PaymentConfirmInformationFragment : PVFragment<FragmentConfirmPaymentBinding>() {
	private var cardSelected: CardModel? = null
	private val repository = PVRepository()
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
			cardSelected = requireArguments().getParcelable<CardModel>("card")?.apply {
				tvLabelCard.text = cardType ?: type
				tvNumberCard.text = numberCard
				tvCardBalance.text = getString(
					R.string.current_balance,
					availableBalance.formatStringCurrency()
				)
			}
			MasterModel.getInstance().apply {
				tvNumberOrder.text = idOrder
				tvCurrentBalance.text = "${orderCurrency?.formatStringCurrency()}đ"
				tvContentOrder.text = getString(R.string.content_order_desc, idOrder, clientId)
				//show alert if error card not enough money
				val cardSelectedCurrency = cardSelected?.availableBalance ?: "0"
				val currencyOrder = orderCurrency ?: "0"
				if (cardSelectedCurrency.toBigDecimal()
						.minus(currencyOrder.toBigDecimal()) < "0".toBigDecimal()
				) {
					tvAlertCard.visibility = View.VISIBLE
					btnConfirm.isEnabled = false
				}
			}
			btnConfirm.setOnClickListener {
				cardSelected?.let {
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
			repository.observableMethodsDetail.observe(
				viewLifecycleOwner,
				Observer {
					hideLoading()
					it?.let {
						startPurchase()
					}
				}
			)
			repository.observableCard.observe(
				viewLifecycleOwner,
				Observer {
					it?.let {
						hideLoading()
						if (it.cardStatus.toInt() == 0) {
							startPurchase()
						}
					}
				}
			)
			repository.observablePurchase.observe(
				viewLifecycleOwner,
				Observer {
					it?.let {
						hideLoading()
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
				}
			)
			repository.error.observe(
				viewLifecycleOwner,
				Observer {
					it?.let {
						hideLoading()
						showAlertError(it)
					}
				}
			)
		}
	}
	
	private fun checkStatusCard(cardModel: CardModel) {
		showLoading()
		if (cardModel.type == "account"){
			repository.getMethodsDetail(cardModel.type ?: "", cardModel.source ?: "")
		} else {
			repository.getCard(cardModel.source ?: "")
		}
	}
	
	private fun showAlertError(error: Pair<Int, String>) {
		AlertPopup.show(
			fragmentManager = childFragmentManager,
			title = "Thông báo",
			primaryTitle = "ok",
			primaryButtonListener = object : AlertPopup.PrimaryButtonListener{
				override fun onClickListener(v: View) {
					if (error.first in 400..499){
						openFragment(
							AuthWebLoginFragment::class.java,
							Bundle(),
							false
						)
					}
				}
			},
			message = error.second
		)
	}
	
	private fun startPurchase() {
		showLoading()
		cardSelected?.let {
			repository.purchase(it)
		}
	}
	
	override fun onBack(): Boolean {
		return false
	}

	override fun onStop() {
		super.onStop()
		repository.clear()
	}
}