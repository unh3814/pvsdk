package com.pvcombank.sdk.ekyc.view.register.home

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.pvcombank.sdk.ekyc.base.PVFragment
import com.pvcombank.sdk.ekyc.databinding.FragmentHomeBinding
import com.pvcombank.sdk.ekyc.model.MasterModel
import com.pvcombank.sdk.ekyc.view.login.AuthWebLoginFragment
import com.pvcombank.sdk.ekyc.view.popup.AlertPopup
import com.pvcombank.sdk.ekyc.view.register.after_create.AfterCreateFragment

class HomeFragment : PVFragment<FragmentHomeBinding>() {
	private val listPermission = listOf(
		android.Manifest.permission.CALL_PHONE,
		android.Manifest.permission.INTERNET,
		android.Manifest.permission.CAMERA
	)
	private val isPermissionSuccess get() = listPermission.all {
		ContextCompat.checkSelfPermission(
			requireContext(),
			it
		) == PackageManager.PERMISSION_GRANTED
	}
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		viewBinding = FragmentHomeBinding.inflate(inflater, container, false)
		return viewBinding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewBinding.apply {
			topBar.hide()
			root.setOnClickListener {
				hideKeyboard()
			}
			hideLoading()
			btnToLogin.setOnClickListener {
				openFragment(
					AuthWebLoginFragment::class.java,
					arguments ?: Bundle(),
					true
				)
			}
			
			btnToRegister.setOnClickListener {
				openFragment(
					AfterCreateFragment::class.java,
					arguments ?: Bundle(),
					true
				)
			}
		}
	}
	
	override fun onStart() {
		super.onStart()
		requireActivity().supportFragmentManager.fragments.clear()
		if (!isPermissionSuccess) {
			ActivityCompat.requestPermissions(
				requireActivity(),
				listPermission.toTypedArray(),
				1
			)
		}
		MasterModel.getInstance().cache.clear()
	}
	
	override fun onBack(): Boolean {
		requireActivity().finish()
		return true
	}
	
	override fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<out String>,
		grantResults: IntArray
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		if (!isPermissionSuccess) {
			AlertPopup.show(
				fragmentManager = childFragmentManager,
				message = "Bạn cần cấp quyền để sử dụng tính năng này",
				primaryTitle = "Đóng",
				primaryButtonListener = object : AlertPopup.PrimaryButtonListener {
					override fun onClickListener(v: View) {
						requireActivity().finish()
					}
				}
			)
		}
	}
}