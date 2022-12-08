package com.pvcombank.sdk.payment.view.otp.select_card

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.pvcombank.sdk.R
import com.pvcombank.sdk.payment.base.PVFragment
import com.pvcombank.sdk.databinding.FragmentPaymentInformationBinding
import com.pvcombank.sdk.payment.model.CardModel
import com.pvcombank.sdk.payment.model.MasterModel
import com.pvcombank.sdk.payment.repository.PVRepository
import com.pvcombank.sdk.payment.util.Utils.formatStringCurrency
import com.pvcombank.sdk.payment.view.login.AuthWebLoginFragment
import com.pvcombank.sdk.payment.view.otp.confirm_card.PaymentConfirmInformationFragment
import com.pvcombank.sdk.payment.view.popup.AlertPopup

class PaymentInformationFragment : PVFragment<FragmentPaymentInformationBinding>(),
                                   ListCardAdapter.CardListener {
	private val listCard = mutableListOf<CardModel>()
	private val repository = PVRepository()
	
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
				openFragment(
					PaymentConfirmInformationFragment::class.java,
					requireArguments(),
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
				tvContentOrder.text = getString(R.string.content_order_desc, idOrder, "vietsens")
				orderDesc = tvContentOrder.text.toString()
			}
			getListCard()
			repository.observableMethods.observe(
				viewLifecycleOwner,
				Observer {
					it?.let {
						hideLoading()
						listCard.clear()
						listCard.addAll(it)
						requireArguments().getParcelable<CardModel>("card")?.let {item ->
							tvLabelCard.text = "${item.cardType ?: ""} ${item.numberCard}"
							tvCardBalance.text = "Số dư: ${item.availableBalance.formatStringCurrency()}đ"
						} ?: kotlin.run {
							listCard.first().isSelected = true
							onCardClick(listCard.first())
						}
						Log.d("Methods", "$it")
					}
				}
			)
			repository.observableListCard.observe(
				viewLifecycleOwner,
				Observer {
					it?.let {
						hideLoading()
						listCard.addAll(it)
						Log.d("ListCard", "$it")
					}
				}
			)
			repository.error.observe(
				viewLifecycleOwner,
				Observer {
					it?.let {
						hideLoading()
						AlertPopup.show(
							fragmentManager = childFragmentManager,
							title = "Thông báo",
							message = it.second,
							primaryTitle = "ok",
							primaryButtonListener = object : AlertPopup.PrimaryButtonListener{
								override fun onClickListener(v: View) {
									MasterModel.getInstance().errorString.onNext(it.second)
									requireActivity().finish()
								}
							}
						)
					}
				}
			)
		}
	}
	
	private fun getListCard() {
		showLoading()
		repository.getMethods()
	}
	
	@SuppressLint("SetTextI18n")
	override fun onCardClick(item: CardModel) {
		listCard.find { item.numberCard == it.numberCard }?.isSelected = true
		viewBinding.apply {
			tvLabelCard.text = "${item.cardType ?: ""} ${item.numberCard}"
			tvCardBalance.text = "Số dư: ${item.availableBalance.formatStringCurrency()}đ"
			requireArguments().putParcelable("card", item)
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

	override fun onStop() {
		super.onStop()
		repository.clear()
	}
}