package com.pvcombank.sdk.ekyc.view.register.after_create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.pvcombank.sdk.ekyc.R
import com.pvcombank.sdk.ekyc.base.PVFragment
import com.pvcombank.sdk.ekyc.databinding.FragmentCreateAccoutBinding
import com.pvcombank.sdk.ekyc.model.Constants
import com.pvcombank.sdk.ekyc.model.MarcomEvent
import com.pvcombank.sdk.ekyc.model.MasterModel
import com.pvcombank.sdk.ekyc.repository.AuthRepository
import com.pvcombank.sdk.ekyc.repository.OnBoardingRepository
import com.pvcombank.sdk.ekyc.util.Utils.timeToString
import com.pvcombank.sdk.ekyc.view.otp.confirm_otp.AuthOTPFragment
import com.pvcombank.sdk.ekyc.view.popup.AlertPopup
import java.util.Date

class AfterCreateFragment : PVFragment<FragmentCreateAccoutBinding>() {
    private var repository: AuthRepository? = null
    private var onBoardingRepository: OnBoardingRepository? = null
    private val cache get() = MasterModel.getInstance().cache

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentCreateAccoutBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.apply {
            MasterModel.getInstance().timeLogin = null
            topBar.show()
            topBar.setTitle("Điền thông tin cơ bản")
            repository = AuthRepository()
            onBoardingRepository = OnBoardingRepository()
            root.setOnClickListener {
                hideKeyboard()
            }
            MasterModel.getInstance().cleanOCR()
            root.setOnClickListener {
                hideKeyboard()
            }
            phoneNumber.addTextChangeListener {
                it?.let {
                    val str = it.toString()
                    if (str.matches(Constants.regexPhone) && str.startsWith("0")) {
                        cache["phone_number"] = it.toString()
                        phoneNumber.setError("")
                    } else if (str.isNullOrEmpty()) {
                        phoneNumber.setError("")
                    } else {
                        phoneNumber.setError("Số điện thoại không hợp lệ, vui lòng thử lại")
                    }
					btnCreateAccount.isEnabled = validateData((cache["email"] as? String),(cache["phone_number"] as? String))
				} ?: kotlin.run {
                    phoneNumber.setError("Vui lòng nhập số điện thoại")
                    cache.remove("phone_number")
                    btnCreateAccount.isEnabled = false
                }
            }
            emailAddress.addTextChangeListener {
                it?.let {
                    val str = it.toString()
                    if (str.matches(Constants.regexEmail)) {
                        cache["email"] = it.toString()
                        emailAddress.setError("")
//                        if (phoneNumber.getText()
//                                .matches(Constants.regexPhone) && phoneNumber.getText()
//                                .startsWith("0")
//                        ) {
//                            btnCreateAccount.isEnabled = true
//                        }
                    } else if (str.isNullOrEmpty()) {
                        emailAddress.setError("")
//                        if (phoneNumber.getText()
//                                .matches(Constants.regexPhone) && phoneNumber.getText()
//                                .startsWith("0")
//                        ) {
//                            btnCreateAccount.isEnabled = true
//                        }
                    } else {
                        emailAddress.setError("Email không đúng định dạng, vui lòng thử lại")
                    }
					btnCreateAccount.isEnabled = validateData((cache["email"] as? String),(cache["phone_number"] as? String))
                } ?: kotlin.run {
                    cache.remove("email")
                    btnCreateAccount.isEnabled = false
                    emailAddress.setError("Email không đúng định dạng, vui lòng thử lại")
                }
            }
            btnCreateAccount.setOnClickListener {
                val cachePhoneNumber = (cache["phone_number"] as? String) ?: ""
                if (cachePhoneNumber.isEmpty() || cachePhoneNumber.length != 10) {
                    phoneNumber.setError("Số điện thoại không hợp lệ, vui lòng thử lại")
                } else {
                    showLoading()
                    repository?.sendOTP(
                        phoneNumber = (cache["phone_number"] as? String) ?: "",
                        email = (cache["email"] as? String) ?: ""
                    )
                    logEvent(
                        this@AfterCreateFragment::class.java.simpleName,
                        MarcomEvent.REGISTRATION_NEXT_FORM,
                        mutableMapOf(
                            Pair("af_email", (cache["email"] as? String) ?: ""),
                            Pair("af_phone", (cache["phone_number"] as? String) ?: "")
                        )
                    )
                }
            }
            repository?.observerSendOTPResponse?.observe(
                viewLifecycleOwner,
                Observer {
                    it?.let {
                        MasterModel.getInstance().uuidOfOTP = it.uuid
                        hideLoading()
                        openFragment(
                            AuthOTPFragment::class.java,
                            requireArguments()
                        )
                        logEvent(
                            this@AfterCreateFragment::class.java.simpleName,
                            MarcomEvent.OPEN_OTP_FORM,
                            mutableMapOf(
                                Pair("af_sent_otp_status", true),
                                Pair("af_sent_otp_time", Date().time.timeToString(Constants.MARCOM_DATE_TIME))
                            )
                        )
                    }
                }
            )
            repository?.error?.observe(
                viewLifecycleOwner,
                Observer {
                    it?.let {
                        logEvent(
                            this@AfterCreateFragment::class.java.simpleName,
                            MarcomEvent.OPEN_OTP_FORM,
                            mutableMapOf(
                                Pair("af_sent_otp_status", false),
                                Pair("af_sent_otp_time", Date().time.timeToString(Constants.MARCOM_DATE_TIME))
                            )
                        )
                        hideLoading()
                        AlertPopup.show(
                            fragmentManager = childFragmentManager,
                            message = "${it.second}",
                            primaryTitle = getString(R.string.txt_close),
                            primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
                                override fun onClickListener(v: View) {

                                }
                            }
                        )
                    }
                }
            )
            logEvent(
                this@AfterCreateFragment::class.java.simpleName,
                MarcomEvent.OPEN_REGISTRATION_FORM,
                mutableMapOf()
            )
        }
    }

	private fun validateData(email: String?, phone: String?): Boolean{
		return email?.matches(Constants.regexEmail) == true && phone?.matches(Constants.regexPhone) == true
	}

    override fun onStart() {
        super.onStart()
        viewBinding.apply {
            phoneNumber.setText((cache["phone_number"] as? String) ?: "")
            emailAddress.setText((cache["email"] as? String) ?: "")
        }
        hideLoading()
    }

    override fun onStop() {
        super.onStop()
        repository?.clear()
    }

    override fun onBack(): Boolean {
        MasterModel.getInstance().errorString.onNext("Cancel")
        requireActivity().finish()
        return true
    }
}