package com.pvcombank.sdk.ekyc.view.register.information

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.LinearLayout.LayoutParams
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.pvcombank.sdk.ekyc.R
import com.pvcombank.sdk.ekyc.base.PVFragment
import com.pvcombank.sdk.ekyc.databinding.FragmentRegisterBinding
import com.pvcombank.sdk.ekyc.databinding.TooltipsCustomBinding
import com.pvcombank.sdk.ekyc.model.BranchModel
import com.pvcombank.sdk.ekyc.model.Constants
import com.pvcombank.sdk.ekyc.model.MasterModel
import com.pvcombank.sdk.ekyc.model.request.RequestFinish
import com.pvcombank.sdk.ekyc.util.SearchUtil
import com.pvcombank.sdk.ekyc.util.Utils.getListBranch
import com.pvcombank.sdk.ekyc.util.Utils.handleUrlClicks
import com.pvcombank.sdk.ekyc.util.Utils.onDrawableClick
import com.pvcombank.sdk.ekyc.util.Utils.toPVDate
import com.pvcombank.sdk.ekyc.view.register.select_branch.SelectBranchBottomSheet
import java.text.SimpleDateFormat
import java.util.*

class InformationRegisterFragment : PVFragment<FragmentRegisterBinding>() {
	private var tooltips: Int? = null
	private val requestFinish = RequestFinish()
	private val cache get() = MasterModel.getInstance().cache
	private var branchCurrent: BranchModel? = null
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		viewBinding = FragmentRegisterBinding.inflate(inflater, container, false)
		return viewBinding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewBinding.apply {
			topBar.show()
			topBar.setTitle("Đăng ký dịch vụ")
			container.setOnClickListener {
				hideKeyboard()
			}
			val data = MasterModel.getInstance().getDataOCR()
			name.setText(data.name)
			sex.setText(
				if (data.gender == true) {
					"Nam"
				} else {
					"Nữ"
				}
			)
			data.dob?.let {
				dob.setText(it.toPVDate())
			}
//			country.setText("Việt Nam")
			identity.setText(data.idNumber)
			issuedBy.setText(data.issuePlace)
			data.issueDate?.let {
				issuedDate.setText(it.toPVDate())
			}
			data.expDate?.let {
				expDate.setText(it.toPVDate())
			}
			if(data.permanentAddress?.isNotEmpty() == true){
//				edtContract.setText(data.permanentAddress)
				autoUpdateBranch(data.permanentAddress ?: "")
			}
			permanentAddress.setText(data.permanentAddress)
			titleCommonInformation.setViewExpend(containerCommonInformation)
			edtBranch.setOnClickListener {
				SelectBranchBottomSheet(branchCurrent) {
					edtBranch.setText(it.bRANCHNAME)
					requestFinish.branchCode = it.bRANCHCODE
				}.show(
					childFragmentManager,
					"SELECT_BRANCH_NAME"
				)
			}
			edtBranch.addTextChangedListener {
				btnConfirm.isEnabled = validate()
			}
			residencyVietnam.setOnCheckedChangeListener { buttonView, isChecked ->
				requestFinish.reside = isChecked
			}
			fatca.setOnCheckedChangeListener { buttonView, isChecked ->
				requestFinish.fatca = isChecked
			}
			val rulesChecked = CompoundButton.OnCheckedChangeListener { view, checked ->
				btnConfirm.isEnabled = validate()
			}
			cbCheckRules1.setOnCheckedChangeListener(rulesChecked)
			cbCheckRules2.setOnCheckedChangeListener(rulesChecked)
			cbCheckRules2.handleUrlClicks() { link ->
				val target = Intent().also {
					it.action = Intent.ACTION_VIEW
					it.setDataAndType(Uri.parse(link), "application/pdf")
				}
				startActivity(Intent.createChooser(target, "Select"))
			}
//			cbCheckRules3.setOnCheckedChangeListener(rulesChecked)
//			if(Constants.CLIENT_ID == "vietsens-sdk"){
//				cbCheckRules3.visibility = View.VISIBLE
//			} else {
//				cbCheckRules3.visibility = View.GONE
//			}
			onDrawableClick(pvcbSms, Constants.DRAWABLE_RIGHT) {
			}
			onDrawableClick(pvcbOnline, Constants.DRAWABLE_RIGHT) {
				showTooltips(pvcbOnline, getString(R.string.tooltips_pvcb_internet_banking))
			}
			fatcaTooltips.setOnClickListener{
				showTooltips(fatcaTooltips, getString(R.string.tooltips_pvcb_internet_banking))
			}
			edtUser.addTextChangedListener {
				requestFinish.introducer = it.toString()
			}
			btnConfirm.setOnClickListener {
				val date = if (data.expDate.isNullOrEmpty()) {
					val tDate = SimpleDateFormat(Constants.TIME_FORMAT).parse(data.issueDate)
					val calendar = Calendar.getInstance()
					calendar.time = tDate
					calendar.add(Calendar.YEAR, 15)
					calendar.time
				} else {
					SimpleDateFormat(Constants.TIME_FORMAT).parse(data.expDate)
				}
				requestFinish.expiredDate = date.toPVDate()
				cache["data_finish"] = requestFinish
				requireArguments().putParcelable("request_data_finish", requestFinish)
				openFragment(
					PasswordRegisterFragment::class.java,
					requireArguments()
				)

//				repository.finish(requestFinish) {
//				}
			}
			root.setOnClickListener {
				hideKeyboard()
			}
			
			requestFinish.job = "Khác"
			requestFinish.gender = if (data.gender == true) "MALE" else "FEMALE"
			requestFinish.currentAddress = data.permanentAddress ?: ""
			requestFinish.permanentAddr = data.permanentAddress ?: ""
			requestFinish.nativePlace = data.nativePlace ?: ""
			requestFinish.reside = residencyVietnam.isChecked
			requestFinish.fatca = fatca.isChecked
			requestFinish.signature = data.signature ?: ""
			requestFinish.expiredDate = data.expDate ?: ""
		}
	}
	
	private fun FragmentRegisterBinding.validate(): Boolean {
		return cbCheckRules1.isChecked
				&& cbCheckRules2.isChecked
//				&& cbCheckRules3.isChecked
				&& residencyVietnam.isChecked
				&& fatca.isChecked
				&& edtBranch.text.isNotEmpty()
//				&& edtContract.text.isNotEmpty()
	}
	
	private fun showTooltips(anchorView: View, message: String) {
		hideKeyboard()
		if (tooltips == anchorView.id) return
		tooltips = anchorView.id
		val viewBinding = TooltipsCustomBinding.inflate(layoutInflater)
		viewBinding.apply {
			content.text = message
		}
		val popupWidth = anchorView.width
		val popupWindow = PopupWindow(viewBinding.root, popupWidth, LayoutParams.WRAP_CONTENT, true)
		popupWindow.setBackgroundDrawable(
			ContextCompat.getDrawable(
				requireContext(),
				R.drawable.bg_tooltips
			)
		)
		popupWindow.setOnDismissListener {
			tooltips = null
		}
		val startX = (anchorView.width - popupWidth) + anchorView.x
		popupWindow.showAsDropDown(anchorView, startX.toInt(), viewBinding.root.height)
	}
	
	private fun filterBranchByString(address: String, edtBranch: EditText) {
		getListBranch()?.let {
			SearchUtil(
				it.toMutableList(),
				object : SearchUtil.SearchFunc<BranchModel> {
					override fun filter(dataItem: BranchModel, stringItem: String): Boolean {
						val rawItem = SearchUtil.convertNonSign(dataItem.aDDRESS.lowercase(Locale.ROOT))
						val comparedStr = SearchUtil.convertNonSign(stringItem)!!
						return rawItem?.contains(comparedStr, true) ?: false
					}
					
					override fun limit(dataItem: BranchModel, stringItem: String): Boolean {
						val rawItem = SearchUtil.convertNonSign(dataItem.aDDRESS.lowercase(Locale.ROOT))
						val comparedStr = SearchUtil.convertNonSign(stringItem)!!
						return rawItem?.contains(comparedStr, true) ?: false
					}
				}
			).apply {
				search(address).observe(
					viewLifecycleOwner,
					androidx.lifecycle.Observer {
						it.firstOrNull()?.let { branch ->
							edtBranch.setText(branch.bRANCHNAME)
							requestFinish.branchCode = branch.bRANCHCODE
							branchCurrent = branch
						} ?: kotlin.run {
							edtBranch.setText("")
							requestFinish.branchCode = ""
							branchCurrent = null
						}
					}
				)
			}
		}
	}
	
	private fun FragmentRegisterBinding.autoUpdateBranch(address: String) {
		handler.removeCallbacksAndMessages(null)
		handler.postDelayed(
			{
				filterBranchByString(address, edtBranch)
			},
			500L
		)
	}
	
	override fun onBack(): Boolean = false
}