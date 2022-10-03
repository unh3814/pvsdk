package com.pvcombank.sdk.view.register.information

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.widget.addTextChangedListener
import com.pvcombank.sdk.R
import com.pvcombank.sdk.base.PVFragment
import com.pvcombank.sdk.databinding.FragmentRegisterBinding
import com.pvcombank.sdk.model.MasterModel
import com.pvcombank.sdk.util.Utils.toPVDate
import com.pvcombank.sdk.view.popup.AlertPopup
import com.pvcombank.sdk.view.register.select_branch.SelectBranchBottomSheet

class InformationRegisterFragment : PVFragment<FragmentRegisterBinding>() {
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
			phoneNumber.setText(data.mobilePhone ?: MasterModel.getInstance().cacheCreateAccountPhone)
			edtContract.setText(data.permanentAddress)
			permanentAddress.setText(data.permanentAddress)
			titleCommonInformation.setViewExpend(containerCommonInformation)
			edtBranch.setOnClickListener {
				SelectBranchBottomSheet{
					edtBranch.setText(it.bRANCHNAME)
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
						title = "Thông báo",
						message = "Quÿ khách vui löng tói chi nhánh PVcomBank\n" +
								"gân nhât dé däng ky và sü dung dich vu hoàc\n" +
								"liên he töng dai: 1900 5555 92",
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
						title = "Thông báo",
						message = "Quÿ khách vui löng tói chi nhánh PVcomBank\n" +
								"gân nhât dé däng ky và sü dung dich vu hoàc\n" +
								"liên he töng dai: 1900 5555 92",
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
			}
			cbCheckRules1.setOnCheckedChangeListener(rulesChecked)
			cbCheckRules2.setOnCheckedChangeListener(rulesChecked)
			pvcOnline.setOnCheckedChangeListener(rulesChecked)
			yes.setOnCheckedChangeListener(rulesChecked)
			aNo.setOnCheckedChangeListener(rulesChecked)
			btnConfirm.setOnClickListener {
				openFragment(
					PasswordRegisterFragment::class.java,
					Bundle(),
					true
				)
			}
			root.setOnClickListener {
				hideKeyboard()
			}
		}
	}
	
	fun FragmentRegisterBinding.validate(): Boolean {
		return cbCheckRules1.isChecked
				&& cbCheckRules2.isChecked
				&& pvcOnline.isChecked
				&& yes.isChecked
				&& aNo.isChecked
				&& edtBranch.text.isNotEmpty()
	}
	
	override fun onBack(): Boolean = false
}