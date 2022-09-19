package com.pvcombank.sdk.view.otp.select_card

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pvcombank.sdk.databinding.FragmentBottomSheetCardBinding
import com.pvcombank.sdk.model.CardModel

class BottomSheetSelectedCard : BottomSheetDialogFragment() {
	private lateinit var viewBinding: FragmentBottomSheetCardBinding
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		viewBinding = FragmentBottomSheetCardBinding.inflate(inflater, container, false)
		return viewBinding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewBinding.apply {
			(parentFragment as? ListCardAdapter.CardListener)?.let { it ->
				ListCardAdapter(it).apply {
					arguments?.getParcelableArray("data")?.toList()
						?.map { model -> model as CardModel }
						?.also {
							setList(it)
						}
					rcvListCard.adapter = this
				}
			}
		}
	}
	
	companion object {
		fun show(fragmentManager: FragmentManager, listCard: List<CardModel>) {
			val fragment = BottomSheetSelectedCard()
			fragment.arguments = Bundle().apply {
				putParcelableArray("data", listCard.toTypedArray())
			}
			fragment.show(
				fragmentManager,
				BottomSheetSelectedCard::class.java.simpleName
			)
		}
	}
}