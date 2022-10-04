package com.pvcombank.sdk.view.register.confirm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.pvcombank.sdk.base.PVFragment
import com.pvcombank.sdk.databinding.FragmentCardCaptureResultBinding
import com.pvcombank.sdk.model.MasterModel
import com.pvcombank.sdk.model.response.ResponseOCR
import com.pvcombank.sdk.util.Utils.toPVDate
import com.pvcombank.sdk.view.register.guide.card.GuideCardIdFragment
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
			liveIn.addTextChangedListener {
				it?.let {
					data.permanentAddress = it.toString()
				}
				validate()
			}
			
			village.setText(data.nativePlace)
			village.addTextChangedListener {
				it?.let {
					data.nativePlace = it.toString()
				}
				validate()
			}
			if ((arguments?.getString("type_card")?.contains("passport") == true) || data.nativePlace.isNullOrEmpty() || data.permanentAddress.isNullOrEmpty()) {
				village.isFocusable = true
				village.isClickable = true
				liveIn.isFocusable = true
				liveIn.isClickable = true
			} else {
				village.isFocusable = false
				village.isClickable = false
				liveIn.isFocusable = false
				liveIn.isClickable = false
			}
			btnConfirm.setOnClickListener {
				openFragment(
					InformationRegisterFragment::class.java,
					Bundle(),
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
	
	override fun onBack(): Boolean = false
}