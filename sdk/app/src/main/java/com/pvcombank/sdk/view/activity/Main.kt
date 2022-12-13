package com.pvcombank.sdk.view.activity

import android.os.Bundle
import com.pvcombank.sdk.base.PVActivity
import com.pvcombank.sdk.ekyc.databinding.ActivityMainBinding
import com.pvcombank.sdk.util.Utils.openFragment
import com.pvcombank.sdk.view.fragment.FragmentHome

class Main : PVActivity<ActivityMainBinding>() {
    override fun onBack(): Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        openFragment(
            FragmentHome::class.java,
            Bundle()
        )
    }
}