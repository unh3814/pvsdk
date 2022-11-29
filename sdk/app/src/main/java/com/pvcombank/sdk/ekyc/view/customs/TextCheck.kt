package com.pvcombank.sdk.ekyc.view.customs

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import com.pvcombank.sdk.ekyc.R
import com.pvcombank.sdk.ekyc.databinding.TextviewCheckBinding

class TextCheck(
    context: Context,
    attributeSet: AttributeSet
) : LinearLayoutCompat(context, attributeSet) {
    private val viewBinding = TextviewCheckBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )
    private var text: String? = null

    //state: uncheck | check | checked -> String
    private var state: String? = "uncheck"

    init {
        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.TextViewCheck,
            0,
            0
        ).apply {
            try {
                text = getString(R.styleable.TextViewCheck_android_text)
                setText(text ?: "")

                state = getString(R.styleable.TextViewCheck_stateView)
                setStateView(state ?: "uncheck")
            } finally {
                recycle()
            }
        }
    }

    fun setStateView(state: String) {
        this.state = state
        with(viewBinding) {
            when (this@TextCheck.state) {
                "checked" -> {
                    btnStateChecked.visibility = View.VISIBLE
                    btnStateCheck.visibility = View.GONE
                    btnStateUncheck.visibility = View.GONE
                }
                "check" -> {
                    btnStateChecked.visibility = View.GONE
                    btnStateCheck.visibility = View.VISIBLE
                    btnStateUncheck.visibility = View.GONE
                }
                else -> {
                    btnStateChecked.visibility = View.GONE
                    btnStateCheck.visibility = View.GONE
                    btnStateUncheck.visibility = View.VISIBLE
                }
            }
        }
    }

    fun setText(text: String) {
        this.text = text
        with(viewBinding) {
            btnStateCheck.text = this@TextCheck.text
            btnStateUncheck.text = this@TextCheck.text
        }
    }
}