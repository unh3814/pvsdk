package com.pvcombank.sdk.view.register.confirm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import com.pvcombank.sdk.base.PVFragment
import com.pvcombank.sdk.databinding.FragmentCardCaptureResultBinding
import com.pvcombank.sdk.model.MasterModel
import com.pvcombank.sdk.model.response.ResponseOCR
import com.pvcombank.sdk.util.Utils.toPVDate
import com.pvcombank.sdk.view.popup.AlertPopup
import com.pvcombank.sdk.view.register.guide.card.GuideCardIdFragment
import com.pvcombank.sdk.view.register.home.HomeFragment
import com.pvcombank.sdk.view.register.information.InformationRegisterFragment

class InformationConfirmFragment : PVFragment<FragmentCardCaptureResultBinding>() {
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		viewBinding = FragmentCardCaptureResultBinding.inflate(inflater, container, false)
		return viewBinding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewBinding.apply {
			topBar.show()
			topBar.setTitle("Xác nhận thông tin")
			if (requireArguments().getBoolean("hide_back")){
				topBar.hideButtonBack()
			} else {
				topBar.showButtonBack()
			}
			root.setOnClickListener {
				hideKeyboard()
			}
			container.setOnClickListener {
				hideKeyboard()
			}
			val data = MasterModel.getInstance().getDataOCR()
			name.setText(data.name ?: "")
			data.dob?.let {
				dateOfBirth.setText(it.toPVDate())
			}
			selectSex.check(
				if (data.gender == true) {
					man.id
				} else {
					women.id
				}
			)
			selectSex.setOnCheckedChangeListener { group, checkedId ->
				MasterModel.getInstance().getDataOCR().gender = checkedId == man.id
			}
			identity.setText(data.idNumber)
			data.issueDate?.let {
				dateOfRange.setText(it.toPVDate())
			}
			data.expDate?.let {
				dateDuo.setText(it.toPVDate())
			}
			issuedBy.setText(data.issuePlace)
			liveIn.setText(data.permanentAddress)
			tvIssuedPlace.text = data.permanentAddress
			liveIn.addTextChangedListener {
				it?.let {
					data.permanentAddress = it.toString()
				}
				validate()
			}
			
			village.setText(data.nativePlace)
			tvPrimaryIssue.text = data.nativePlace
			village.addTextChangedListener {
				it?.let {
					data.nativePlace = it.toString()
				}
				validate()
			}
			if ((requireArguments().getString("type_card")?.contains("passport") == true)){
				village.visibility = View.VISIBLE
				liveIn.visibility = View.VISIBLE
				tvPrimaryIssue.visibility = View.GONE
				tvIssuedPlace.visibility = View.GONE
			} else {
				village.visibility = View.GONE
				liveIn.visibility = View.GONE
				tvPrimaryIssue.visibility = View.VISIBLE
				tvIssuedPlace.visibility = View.VISIBLE
			}
			btnConfirm.setOnClickListener {
				openFragment(
					InformationRegisterFragment::class.java,
					requireArguments(),
					true
				)
			}
			validate()
		}
	}
	
	fun validate() {
		val data = MasterModel.getInstance().getDataOCR()
		viewBinding.btnConfirm.isEnabled = (
				data.name?.isNotEmpty() == true &&
						data.idNumber?.isNotEmpty() == true &&
						data.issueDate?.isNotEmpty() == true &&
						data.expDate?.isNotEmpty() == true &&
						data.issuePlace?.isNotEmpty() == true &&
						data.permanentAddress?.isNotEmpty() == true &&
						data.nativePlace?.isNotEmpty() == true
				)
	}
	
	override fun onBack(): Boolean {
		if(requireArguments().getBoolean("hide_back")){
			AlertPopup.show(
				fragmentManager = childFragmentManager,
				message = "Bạn muốn dừng không",
				primaryTitle = "OK",
				primaryButtonListener = object : AlertPopup.PrimaryButtonListener{
					override fun onClickListener(v: View) {
						requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
						openFragment(HomeFragment::class.java, Bundle(), true)
					}
				},
				secondTitle = "Không",
				secondButtonListener = object : AlertPopup.SecondButtonListener{
					override fun onClickListener(v: View) {
					
					}
				}
			)
		} else {
			openFragment(GuideCardIdFragment::class.java, Bundle(), true)
		}
		return true
	}
}