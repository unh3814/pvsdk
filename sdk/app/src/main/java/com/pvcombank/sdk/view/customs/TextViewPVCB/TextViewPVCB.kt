package com.pvcombank.sdk.view.customs.TextViewPVCB

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.widget.addTextChangedListener
import com.pvcombank.sdk.R
import com.pvcombank.sdk.databinding.TextviewPvcombankBinding

class TextViewPVCB(context: Context, attributeSet: AttributeSet) : LinearLayoutCompat(
	context,
	attributeSet
) {
	//region variable
	private val binding: TextviewPvcombankBinding = TextviewPvcombankBinding.inflate(
		LayoutInflater.from(context),
		this@TextViewPVCB,
		true
	)
	private var sTitle: String = ""
	private var isTitleEnable = false
	private var sHint: String = ""
	private var isHintEnable = false
	private var sError: String = ""
	private var isErrorEnable = false
	private var sNote: String = ""
	//endregion variable
	
	init {
		context.theme.obtainStyledAttributes(attributeSet, R.styleable.TextViewPVCB, 0, 0).apply {
			try {
				binding.apply {
					sTitle = getString(R.styleable.TextViewPVCB_textTitle) ?: ""
					sHint = getString(R.styleable.TextViewPVCB_textHint) ?: ""
					sError = getString(R.styleable.TextViewPVCB_textError) ?: ""
					sNote = getString(R.styleable.TextViewPVCB_note) ?: ""
					isTitleEnable = getBoolean(R.styleable.TextViewPVCB_titleEnabled, false)
					isHintEnable = getBoolean(R.styleable.TextViewPVCB_hintEnabled, false)
					isErrorEnable = getBoolean(R.styleable.TextViewPVCB_errorEnabled, false)
					setNote()
					setTitle()
					setHint()
					setError()
					editor.addTextChangedListener {
						editor.setBackgroundResource(R.drawable.bg_edit)
					}
				}
			} finally {
				recycle()
			}
		}
	}
	
	fun setTitle(value: String? = null) {
		binding.apply {
			sTitle = (value ?: sTitle)
			if (sTitle.isNotEmpty()) {
				title.visibility = View.VISIBLE
				title.text = sTitle
			} else {
				title.visibility = View.GONE
			}
		}
	}
	
	fun setHint(value: String? = null) {
		binding.apply {
			sHint = (value ?: sHint)
			editor.hint = sHint
		}
	}
	
	fun setNote(value: String? = null) {
		binding.apply {
			sNote = (value ?: sNote)
			if (isErrorEnable) {
				note.visibility = View.GONE
				return
			}
			if ((sError.isEmpty() && sNote.isNotEmpty()) || !isErrorEnable) {
				note.visibility = View.VISIBLE
			}
			note.text = sNote
		}
	}
	
	fun setError(value: String? = null) {
		binding.apply {
			sError = (value ?: sError)
			if (!isErrorEnable) {
				error.visibility = View.INVISIBLE
				return
			}
			if (sError.isNotEmpty()) {
				error.visibility = View.VISIBLE
				error.text = sError
				editor.setBackgroundResource(R.drawable.bg_error)
			} else {
				error.visibility = View.INVISIBLE
			}
		}
	}
	
	fun getText(): String = binding.editor.text.toString()
	
	fun addTextChangeListener(textWatcher: TextWatcher) {
		return binding.editor.addTextChangedListener(textWatcher)
	}
	
	fun addTextChangeListener(afterTextChanged: (text: Editable?) -> Unit) {
		binding.editor.addTextChangedListener {
			afterTextChanged.invoke(it)
		}
	}
}