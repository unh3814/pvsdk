package com.pvcombank.sdk.ekyc.view.register.information

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.LinearLayout.LayoutParams
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pvcombank.sdk.R
import com.pvcombank.sdk.ekyc.base.PVFragment
import com.pvcombank.sdk.databinding.FragmentRegisterBinding
import com.pvcombank.sdk.databinding.TooltipsCustomBinding
import com.pvcombank.sdk.ekyc.model.BranchModel
import com.pvcombank.sdk.ekyc.model.Constants
import com.pvcombank.sdk.ekyc.model.MasterModel
import com.pvcombank.sdk.ekyc.model.request.RequestFinish
import com.pvcombank.sdk.ekyc.util.Utils.getListBranch
import com.pvcombank.sdk.ekyc.util.Utils.handleUrlClicks
import com.pvcombank.sdk.ekyc.util.Utils.onDrawableClick
import com.pvcombank.sdk.ekyc.util.Utils.toPVDate
import com.pvcombank.sdk.ekyc.view.popup.AlertPopup
import com.pvcombank.sdk.ekyc.view.register.select_branch.SelectBranchBottomSheet
import java.text.SimpleDateFormat
import java.util.*

class InformationRegisterFragment : PVFragment<FragmentRegisterBinding>() {
	private var tooltips: Int? = null
	private val requestFinish = RequestFinish()
	private val cache get() = MasterModel.getInstance().cache
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
			country.setText("Việt Nam")
			identity.setText(data.idNumber)
			issuedBy.setText(data.issuePlace)
			data.issueDate?.let {
				issuedDate.setText(it.toPVDate())
			}
			data.expDate?.let {
				expDate.setText(it.toPVDate())
			}
			phoneNumber.setText(
				data.mobilePhone ?: (cache["phone_number"] as? String) ?: ""
			)
			edtContract.setText(data.permanentAddress)
			edtContract.addTextChangedListener {
				btnConfirm.isEnabled = validate()
				requestFinish.currentAddress = it.toString()
				autoUpdateBranch(it.toString())
			}
			permanentAddress.setText(data.permanentAddress)
			titleCommonInformation.setViewExpend(containerCommonInformation)
			edtBranch.setOnClickListener {
				SelectBranchBottomSheet {
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
			checkCuTru.check(yes.id)
			checkCuTru.setOnCheckedChangeListener { group, checkedId ->
				if (checkedId == no.id) {
					AlertPopup.show(
						fragmentManager = childFragmentManager,
						primaryTitle = "Đóng",
						primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
							override fun onClickListener(v: View) {
							}
						}
					)
				}
			}
			checkAmerica.check(aNo.id)
			checkAmerica.setOnCheckedChangeListener { group, checkedId ->
				if (checkedId == aYes.id) {
					AlertPopup.show(
						fragmentManager = childFragmentManager,
						primaryTitle = "Đóng",
						primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
							override fun onClickListener(v: View) {
							}
						}
					)
				}
			}
			val rulesChecked = CompoundButton.OnCheckedChangeListener { _, _ ->
				btnConfirm.isEnabled = validate()
				requestFinish.reside = yes.isChecked == true
				requestFinish.fatca = aYes.isChecked == true
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
			pvcOnline.setOnCheckedChangeListener(rulesChecked)
			yes.setOnCheckedChangeListener(rulesChecked)
			aNo.setOnCheckedChangeListener(rulesChecked)
			onDrawableClick(pvSms, Constants.DRAWABLE_RIGHT) {
			}
			onDrawableClick(pvcOnline, Constants.DRAWABLE_RIGHT) {
				showTooltips(pvcOnline, getString(R.string.tooltips_pvcb_internet_banking))
			}
			edtUser.addTextChangedListener {
				requestFinish.introducer = it.toString()
			}
			btnConfirm.setOnClickListener {
				var date = if (data.expDate.isNullOrEmpty()) {
					val tDate = SimpleDateFormat(Constants.TIME_FORMAT).parse(data.issueDate)
					val calendar = Calendar.getInstance()
					calendar.time = tDate
					calendar.add(Calendar.YEAR, 15)
					calendar.time
				} else {
					SimpleDateFormat(Constants.TIME_FORMAT).parse(data.expDate)
				}
				requestFinish.expiredDate = date.toPVDate()
				requireArguments().putParcelable("request_data_finish", requestFinish)
				openFragment(
					PasswordRegisterFragment::class.java,
					requireArguments(),
					true
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
			requestFinish.reside = yes.isChecked == true
			requestFinish.fatca = aYes.isChecked == true
			requestFinish.signature = data.signature ?: ""
			requestFinish.expiredDate = data.expDate ?: ""
		}
	}
	
	private fun FragmentRegisterBinding.validate(): Boolean {
		return cbCheckRules1.isChecked
				&& cbCheckRules2.isChecked
				&& pvcOnline.isChecked
				&& yes.isChecked
				&& aNo.isChecked
				&& edtBranch.text.isNotEmpty()
				&& edtContract.text.isNotEmpty()
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
	
	private fun filterBranchByString(address: String): LiveData<List<BranchModel>> {
		val result = MutableLiveData<List<BranchModel>>()
		handler.removeCallbacksAndMessages(null)
		handler.postDelayed(
			{
				var listAddress = mutableListOf<BranchModel>().also { list ->
					getListBranch()?.let {
						list.addAll(it)
					}
				}
				val listSplitContactAddress = address.split(",")
				for (i in listSplitContactAddress.size downTo 0) {
					val item = listSplitContactAddress[i]
					val itemConstantInAddress = listAddress.any {
						it.aDDRESS.contains(item)
					}
					if (itemConstantInAddress) {
						listAddress = listAddress.filter {
							it.aDDRESS.contains(item)
						}.toMutableList()
					} else break
				}
				result.postValue(listAddress)
			},
			100L
		)
		return result
	}
	
	private fun FragmentRegisterBinding.autoUpdateBranch(address: String) {
		handler.removeCallbacksAndMessages(null)
		handler.postDelayed(
			{
				filterBranchByString(address).observe(
					viewLifecycleOwner,
					androidx.lifecycle.Observer {
						it?.let {
							edtBranch.setText(it.first().aDDRESS)
						}
					}
				)
			},
			500L
		)
	}
	
	override fun onBack(): Boolean = false
}