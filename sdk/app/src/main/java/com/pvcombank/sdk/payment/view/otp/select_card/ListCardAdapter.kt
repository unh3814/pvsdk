package com.pvcombank.sdk.payment.view.otp.select_card

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pvcombank.sdk.databinding.ItemCardBinding
import com.pvcombank.sdk.payment.model.CardModel
import com.pvcombank.sdk.payment.util.Utils.formatStringCurrency

class ListCardAdapter(private var listener: CardListener) : RecyclerView.Adapter<ListCardAdapter.ListCardAdapterViewHolder>() {
	private val data: MutableList<CardModel> = mutableListOf()
	private var itemViewContext: Context? = null
	
	interface CardListener {
		fun onCardClick(item: CardModel)
	}
	
	inner class ListCardAdapterViewHolder(private var viewBinding: ItemCardBinding) : RecyclerView.ViewHolder(
		viewBinding.root
	) {
		fun bindData(item: CardModel, position: Int) {
			itemViewContext = itemView.context
			viewBinding.apply {
				itemView.setOnClickListener {
					item.isSelected = !item.isSelected
					clearChecked(position)
					listener.onCardClick(item)
					notifyDataSetChanged()
				}
				tvLabel.text = "${item.cardType} ${item.numberCard}"
				tvNumberCard.text =
					"Hạn mức: ${item.availableBalance.toString().formatStringCurrency()}đ"
				if (item.isSelected){
					viewBinding.imgChecked.visibility = View.VISIBLE
				} else {
					viewBinding.imgChecked.visibility = View.INVISIBLE
				}
			}
		}
		
		private fun clearChecked(currentPosition: Int) {
			data.forEach { cardModel ->
				if (cardModel.numberCard != data[currentPosition].numberCard) {
					cardModel.isSelected = false
				}
			}
		}
	}
	
	override fun onCreateViewHolder(
		parent: ViewGroup,
		viewType: Int
	): ListCardAdapterViewHolder {
		val inflater = LayoutInflater.from(parent.context)
		val viewBinding = ItemCardBinding.inflate(inflater, parent, false)
		return ListCardAdapterViewHolder(viewBinding)
	}
	
	override fun onBindViewHolder(
		holder: ListCardAdapterViewHolder,
		position: Int
	) {
		holder.bindData(data[position], position)
	}
	
	override fun getItemCount(): Int = data.size
	
	@SuppressLint("NotifyDataSetChanged")
	fun setList(values: List<CardModel>) {
		data.clear()
		data.addAll(values.toMutableList())
		notifyDataSetChanged()
	}
	
	fun getCurrentChecked(): CardModel? {
		data.forEach {
			if (it.isSelected) {
				return it
			}
		}
		return null
	}
}