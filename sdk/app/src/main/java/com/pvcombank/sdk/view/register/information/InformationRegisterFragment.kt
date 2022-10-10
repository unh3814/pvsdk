package com.pvcombank.sdk.view.register.information

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.LinearLayout.LayoutParams
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.pvcombank.sdk.R
import com.pvcombank.sdk.base.PVFragment
import com.pvcombank.sdk.databinding.FragmentRegisterBinding
import com.pvcombank.sdk.databinding.TooltipsCustomBinding
import com.pvcombank.sdk.model.Constants
import com.pvcombank.sdk.model.MasterModel
import com.pvcombank.sdk.model.ResponseData
import com.pvcombank.sdk.model.request.RequestFinish
import com.pvcombank.sdk.repository.OnBoardingRepository
import com.pvcombank.sdk.util.Utils.handleUrlClicks
import com.pvcombank.sdk.util.Utils.onDrawableClick
import com.pvcombank.sdk.util.Utils.toPVDate
import com.pvcombank.sdk.view.popup.AlertPopup
import com.pvcombank.sdk.view.register.home.HomeFragment
import com.pvcombank.sdk.view.register.select_branch.SelectBranchBottomSheet
import okhttp3.ResponseBody

class InformationRegisterFragment : PVFragment<FragmentRegisterBinding>() {
	private var tooltips: Int? = null
	private val requestFinish = RequestFinish()
	private val repository = OnBoardingRepository()
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
				data.mobilePhone ?: MasterModel.getInstance().cacheCreateAccountPhone
			)
			edtContract.setText(data.permanentAddress)
			edtContract.addTextChangedListener {
				btnConfirm.isEnabled = validate()
				requestFinish.currentAddress = it.toString()
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
				repository.finish(requestFinish) {
					(it["success"] as? ResponseData<*>)?.let {response ->
						if (response.code == "1"){
							openFragment(
								PasswordRegisterFragment::class.java,
								Bundle(),
								true
							)
						} else {
							AlertPopup.show(
								fragmentManager = childFragmentManager,
								title = "Thông báo",
								message = "Đã có lỗi xảy ra.\nVui lòng thực hiện lại sau!",
								primaryButtonListener = object : AlertPopup.PrimaryButtonListener{
									override fun onClickListener(v: View) {
									
									}
								},
								primaryTitle = "OK"
							)						}
					}
					(it["fail"] as? String)?.let { errorStr ->
						AlertPopup.show(
							fragmentManager = childFragmentManager,
							title = "Thông báo",
							message = errorStr,
							primaryButtonListener = object : AlertPopup.PrimaryButtonListener{
								override fun onClickListener(v: View) {
									if (errorStr.contains("403")){
										openFragment(
											HomeFragment::class.java,
											Bundle(),
											true
										)
									}
								}
							},
							primaryTitle = "OK"
						)
					}
				}
			}
			root.setOnClickListener {
				hideKeyboard()
			}
			
			requestFinish.job = "Khác"
			requestFinish.gender = if(data.gender == true) "MALE" else "FEMALE"
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
		popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.bg_tooltips))
		popupWindow.setOnDismissListener {
			tooltips = null
		}
		val startX = (anchorView.width - popupWidth) + anchorView.x
		popupWindow.showAsDropDown(anchorView, startX.toInt(), viewBinding.root.height)
	}
	
	override fun onBack(): Boolean = false
}