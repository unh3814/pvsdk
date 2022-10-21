package com.pvcombank.sdk.ekyc.view.customs.EditViewPVCB

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.widget.addTextChangedListener
import com.pvcombank.sdk.R
import com.pvcombank.sdk.databinding.EditviewPvcombankBinding

class EditViewPVCB(context: Context, attributeSet: AttributeSet) : LinearLayoutCompat(
	context,
	attributeSet
) {
	//region variable
	private val binding: EditviewPvcombankBinding = EditviewPvcombankBinding.inflate(
		LayoutInflater.from(context),
		this@EditViewPVCB,
		true
	)
	private var sTitle: String = ""
	private var isTitleEnable = false
	private var sHint: String = ""
	private var isHintEnable = false
	private var sError: String = ""
	private var isErrorEnable = false
	private var sNote: String = ""
	private var isPassword: Boolean = false
	private var iconPassword: Boolean = false
	private var maxLengh: Int = 0
	private var inputType: String? = "full"
	//endregion variable
	
	init {
		context.theme.obtainStyledAttributes(attributeSet, R.styleable.EditViewPVCB, 0, 0).apply {
			try {
				binding.apply {
					sTitle = getString(R.styleable.EditViewPVCB_textTitle) ?: ""
					sHint = getString(R.styleable.EditViewPVCB_textHint) ?: ""
					sError = getString(R.styleable.EditViewPVCB_textError) ?: ""
					sNote = getString(R.styleable.EditViewPVCB_note) ?: ""
					isTitleEnable = getBoolean(R.styleable.EditViewPVCB_titleEnabled, false)
					isHintEnable = getBoolean(R.styleable.EditViewPVCB_hintEnabled, false)
					isErrorEnable = getBoolean(R.styleable.EditViewPVCB_errorEnabled, false)
					isPassword = getBoolean(R.styleable.EditViewPVCB_isPassword, false)
					iconPassword = getBoolean(R.styleable.EditViewPVCB_isShowPassword, false)
					maxLengh = getInteger(R.styleable.EditViewPVCB_maxLengh, 0)
					inputType = getString(R.styleable.EditViewPVCB_inputType)
					setIsPassword()
					showIconPassword()
					setNote()
					setTitle()
					setHint()
					setError()
					setInputType()
					setMaxLengh()
					editor.addTextChangedListener {
						editor.setBackgroundResource(R.drawable.bg_edit)
						error.visibility = View.GONE
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
	
	fun setText(value: String? = null) {
		binding.apply {
			value?.let {
				editor.setText(value)
			}
		}
	}
	
	fun setNote(value: String? = null) {
		binding.apply {
			sNote = (value ?: sNote)
			if (sNote.isEmpty() || (sError.isNotEmpty() && isErrorEnable)){
				note.visibility = View.GONE
			} else{
				note.visibility = View.VISIBLE
			}
			note.text = sNote
		}
	}
	
	fun setError(value: String? = null) {
		binding.apply {
			sError = (value ?: sError)
			val isShowError = isErrorEnable
					&& sError.isNotEmpty()
			if (isShowError){
				error.visibility = View.VISIBLE
				note.visibility = View.GONE
				error.text = sError
			} else{
				if (sNote.isNotEmpty()){
					error.visibility = View.GONE
					note.visibility = View.VISIBLE
				} else{
					error.visibility = View.INVISIBLE
				}
			}
		}
	}
	
	fun setIsPassword() {
		if (isPassword) {
			binding.editor.transformationMethod = PasswordTransformationMethod()
		} else {
			binding.editor.transformationMethod = null
		}
	}
	
	fun setInputType(){
		when(inputType){
			"full" -> {
				binding.editor.inputType = InputType.TYPE_CLASS_TEXT
			}
			"number" -> {
				binding.editor.inputType = InputType.TYPE_CLASS_NUMBER
			}
		}
	}
	
	fun setMaxLengh(){
		if (maxLengh > 0){
			binding.editor.filters = arrayOf(InputFilter.LengthFilter(maxLengh))
		}
	}
	
	fun showIconPassword() {
		if (iconPassword) {
			binding.editor.setCompoundDrawablesWithIntrinsicBounds(
				0,
				0,
				R.drawable.ic_round_eye_24,
				0
			)
			binding.editor.setOnTouchListener { v, event ->
				val DRAWABLE_LEFT = 0
				val DRAWABLE_TOP = 1
				val DRAWABLE_RIGHT = 2
				val DRAWABLE_BOTTOM = 3
				if (event.action == MotionEvent.ACTION_UP) {
					if (event.getRawX() >= (binding.editor.right - binding.editor.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
						isPassword = !isPassword
						changeDrawablePassword()
						setIsPassword()
						true
					}
				}
				false
			}
		}
	}
	
	private fun changeDrawablePassword() {
		if (isPassword) {
			binding.editor.setCompoundDrawablesWithIntrinsicBounds(
				0,
				0,
				R.drawable.ic_round_eye_24,
				0
			)
		} else {
			binding.editor.setCompoundDrawablesWithIntrinsicBounds(
				0,
				0,
				R.drawable.ic_eye_clean_24,
				0
			)
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