package com.pvcombank.sdk.view.register.select_branch

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pvcombank.sdk.R
import com.pvcombank.sdk.base.PVActivity
import com.pvcombank.sdk.databinding.BottomsheetSelectBranchBinding
import com.pvcombank.sdk.databinding.ItemBranchBinding
import com.pvcombank.sdk.model.BranchModel
import com.pvcombank.sdk.model.Constants
import com.pvcombank.sdk.model.MasterModel

class SelectBranchBottomSheet(private val callBack: (item: BranchModel) -> Unit) : BottomSheetDialogFragment() {
	lateinit var viewBinding: BottomsheetSelectBranchBinding
	private val data = mutableListOf<BranchModel>()
	private val dataResult = mutableListOf<BranchModel>()
	private val handler = Handler(Looper.getMainLooper())
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		viewBinding = BottomsheetSelectBranchBinding.inflate(inflater, container, false)
		return viewBinding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewBinding.apply {
			val adapter = MyAdapter().apply {
				data.addAll(mockupData)
				dataResult.addAll(data)
				this.notifyDataSetChanged()
			}
			btnBack.setOnClickListener {
				dismissAllowingStateLoss()
			}
			edtSearch.addTextChangedListener {
				handler.removeCallbacksAndMessages(null)
				handler.postDelayed(
					{
						dataResult.clear()
						dataResult.addAll(
							data.filter { model ->
								model.bRANCHNAME.contains(it.toString(), true)
							}
						)
						adapter.notifyDataSetChanged()
					},
					300L
				)
				
			}
			rcv.adapter = adapter
		}
	}
	
	override fun onStart() {
		super.onStart()
		dialog?.window
			?.setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
			)
	}
	
	fun onItemClick(item: BranchModel) {
		callBack.invoke(item)
		MasterModel.getInstance().selectBranch = item
		dismissAllowingStateLoss()
	}
	
	inner class MyAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
		lateinit var itemBinding: ItemBranchBinding
		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
			val layoutInflater = LayoutInflater.from(parent.context)
			itemBinding = ItemBranchBinding.inflate(layoutInflater, parent, false)
			return ViewHolder(itemBinding.root)
		}
		
		override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
			(holder as ViewHolder).findData(dataResult[position])
		}
		
		override fun getItemCount(): Int = dataResult.size
	}
	
	inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
		fun findData(item: BranchModel) {
			itemView.setOnClickListener {
				onItemClick(item)
			}
			itemView.findViewById<TextView>(R.id.name).text = item.bRANCHNAME
		}
	}
	
	val mockupData = Gson().fromJson<List<BranchModel>>(
		Constants.mockupBranch,
		object : TypeToken<List<BranchModel>>() {}.type
	)
	
	override fun onDismiss(dialog: DialogInterface) {
		super.onDismiss(dialog)
		(requireActivity() as? PVActivity<*>)?.hideKeyboard()
	}
}
