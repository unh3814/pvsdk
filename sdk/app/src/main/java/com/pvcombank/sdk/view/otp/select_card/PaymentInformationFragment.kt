package com.pvcombank.sdk.view.otp.select_card

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.pvcombank.sdk.R
import com.pvcombank.sdk.base.PVFragment
import com.pvcombank.sdk.databinding.FragmentPaymentInformationBinding
import com.pvcombank.sdk.model.CardModel
import com.pvcombank.sdk.model.MasterModel
import com.pvcombank.sdk.repository.AuthRepository
import com.pvcombank.sdk.util.Utils.formatStringCurrency
import com.pvcombank.sdk.view.login.AuthWebLoginFragment
import com.pvcombank.sdk.view.otp.confirm_card.PaymentConfirmInformationFragment
import com.pvcombank.sdk.view.popup.AlertPopup

class PaymentInformationFragment : PVFragment<FragmentPaymentInformationBinding>(), ListCardAdapter.CardListener {
	private val listCard = mutableListOf<CardModel>()
	private val repository = AuthRepository()
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		viewBinding = FragmentPaymentInformationBinding.inflate(inflater, container, false)
		return viewBinding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		MasterModel.getInstance().isCreateAccount = false
		viewBinding.apply {
			topBar.setTitle(getString(R.string.information_payment))
			topBar.show()
			btnCancelOrder.setOnClickListener {
				AlertPopup.show(
					fragmentManager = childFragmentManager,
					title = "Thông báo",
					message = "Bạn có muốn huỷ giao dịch này không",
					primaryTitle = "OK",
					primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
						override fun onClickListener(v: View) {
							requireActivity().finish()
							MasterModel.getInstance().errorString.onNext("Đơn hàng đã được huỷ theo yêu cầu")
						}
					},
					secondTitle = "Cancel",
					secondButtonListener = object : AlertPopup.SecondButtonListener {
						override fun onClickListener(v: View) = Unit
					}
				)
			}
			btnConfirm.setOnClickListener {
//				val cardSelectedCurrency = listCard.find { it.isSelected }?.availableBalance?.toString() ?: "0"
//				val currencyOrder = MasterModel.getInstance().orderCurrency ?: "0"
//				if (cardSelectedCurrency.toBigDecimal().minus(currencyOrder.toBigDecimal()) < "50000".toBigDecimal()){
//
//				} else {
//					val arg = Bundle().apply {
//						putParcelable("card", listCard.find { it.isSelected })
//					}
//					openFragment(
//						PaymentConfirmInformationFragment::class.java,
//						arg,
//						true
//					)
//				}
				val arg = Bundle().apply {
					putParcelable("card", listCard.find { it.isSelected })
				}
				openFragment(
					PaymentConfirmInformationFragment::class.java,
					arg,
					true
				)
			}
			loItemCard.setOnClickListener {
				if (listCard.isEmpty()) return@setOnClickListener
				BottomSheetSelectedCard.show(childFragmentManager, listCard)
			}
			MasterModel.getInstance().apply {
				tvNumberOrder.text = idOrder
				tvCurrentBalance.text = "${orderCurrency?.formatStringCurrency()}đ"
				tvContentOrder.text = getString(R.string.content_order_desc, idOrder, clientId)
				orderDesc = tvContentOrder.text.toString()
			}
			getListCard()
			repository.onNeedLogin.observe(
				viewLifecycleOwner,
				Observer {
					AlertPopup.show(
						title = "Thông báo",
						message = "Hết thời gian đăng nhập, mời bạn đăng nhập lại",
						primaryTitle = "OK",
						primaryButtonListener = object : AlertPopup.PrimaryButtonListener{
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
	
	private fun getListCard() {
		showLoading()
		repository.getListCard {
			it?.let {
				this.listCard.clear()
				this.listCard.addAll(it)
				if (viewBinding.tvCardBalance.text.isNullOrEmpty()){
					this.listCard.first().isSelected = true
					onCardClick(this.listCard.first())
				}
			} ?: kotlin.run {
				Handler(Looper.getMainLooper())
					.post {
						AlertPopup.show(
							fragmentManager = childFragmentManager,
							title = "Thông báo",
							message = "Lỗi hệ thống, thử lại sau",
							primaryTitle = "ok",
							primaryButtonListener = object : AlertPopup.PrimaryButtonListener{
								override fun onClickListener(v: View) {
									MasterModel.getInstance().errorString.onNext("Lỗi hệ thống")
									requireActivity().finish()
								}
							}
						)
					}
			}
			hideLoading()
		}
	}
	
	@SuppressLint("SetTextI18n")
	override fun onCardClick(item: CardModel) {
		listCard.find { item.numberCard == it.numberCard }?.isSelected = true
		viewBinding.apply {
			tvLabelCard.text = "${item.cardType} ${item.numberCard}"
			tvCardBalance.text = "Số dư: ${item.availableBalance.toString().formatStringCurrency()}đ"
//			MasterModel.getInstance().orderCurrency?.let {
//				if (item.availableBalance.toBigDecimal().minus(it.toBigDecimal()) < "50000".toBigDecimal()){
//					showInlineMessage("Thẻ không đủ số dư")
//				}else {
//					hideInlineMessage()
//				}
//			}
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