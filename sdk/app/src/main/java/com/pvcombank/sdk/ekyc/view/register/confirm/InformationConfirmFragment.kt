package com.pvcombank.sdk.ekyc.view.register.confirm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import com.pvcombank.sdk.ekyc.databinding.FragmentCardCaptureResultBinding
import com.pvcombank.sdk.ekyc.base.PVFragment
import com.pvcombank.sdk.ekyc.model.Constants
import com.pvcombank.sdk.ekyc.model.MasterModel
import com.pvcombank.sdk.ekyc.util.Utils.toPVDate
import com.pvcombank.sdk.ekyc.util.Utils.toSVDate
import com.pvcombank.sdk.ekyc.view.popup.AlertPopup
import com.pvcombank.sdk.ekyc.view.register.guide.card.GuideCardIdFragment
import com.pvcombank.sdk.ekyc.view.register.information.InformationRegisterFragment
import java.text.SimpleDateFormat
import java.util.*

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
			topBar.showButtonBack()
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
			if (data.expDate.isNullOrEmpty()){
				val tDate = SimpleDateFormat(Constants.TIME_FORMAT).parse(data.issueDate)
				val calendar = Calendar.getInstance()
				calendar.time = tDate
				calendar.add(Calendar.YEAR, 15)
				data.expDate = calendar.time.toSVDate()
				dateDuo.setText(data.expDate!!.toPVDate())
			} else {
				data.expDate?.let {
					dateDuo.setText(it.toPVDate())
				}
			}
			issuedBy.setText(data.issuePlace)
			liveInNow.setText(data.permanentAddress)


			primaryIssue.setText(data.nativePlace)
			editAddressCurrent.addTextChangeListener {
				data.permanentAddress = it.toString()
				validate()
			}
			editAddressAlways.addTextChangeListener {
				data.nativePlace = it.toString()
				validate()
			}
			if (
				(requireArguments().getString("type_card")?.contains("passport") == true)
				|| data.nativePlace.isNullOrEmpty()
				|| data.permanentAddress.isNullOrEmpty()
			) {
				liveInNow.visibility = View.GONE
				editAddressAlways.visibility = View.VISIBLE
			}
			btnConfirm.setOnClickListener {
				openFragment(
					InformationRegisterFragment::class.java,
					requireArguments()
				)
			}
			validate()
		}
	}
	
	private fun validate() {
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
		AlertPopup.show(
			fragmentManager = childFragmentManager,
			message = "Bạn có muốn thực hiện lại xác nhận giấy tờ tuỳ thân không?",
			primaryTitle = "Đồng ý",
			primaryButtonListener = object : AlertPopup.PrimaryButtonListener{
				override fun onClickListener(v: View) {
					openFragment(GuideCardIdFragment::class.java, Bundle())
				}
			},
			secondTitle = "Huỷ",
			secondButtonListener = object : AlertPopup.SecondButtonListener{
				override fun onClickListener(v: View) {
				
				}
			}
		)
		return true
	}
}