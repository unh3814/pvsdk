package com.pvcombank.sdk.view.register.scan.face

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.pvcombank.sdk.databinding.ItemViewFaceBinding

class PoseAdapter : RecyclerView.Adapter<PoseAdapterViewHolder>() {
	private val data: MutableList<String> = mutableListOf()
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoseAdapterViewHolder {
		val viewBinding = ItemViewFaceBinding.inflate(
			LayoutInflater.from(parent.context),
			parent,
			false
		)
		return PoseAdapterViewHolder(viewBinding)
	}
	
	override fun onBindViewHolder(holder: PoseAdapterViewHolder, position: Int) {
		holder.bindData()
	}
	
	override fun getItemCount(): Int = data.size
}

class PoseAdapterViewHolder(private var vb: ViewBinding) : RecyclerView.ViewHolder(vb.root) {
	fun bindData() {
	
	}
}