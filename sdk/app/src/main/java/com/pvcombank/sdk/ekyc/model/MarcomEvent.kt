package com.pvcombank.sdk.ekyc.model

object MarcomEvent {
    const val OPEN_REGISTRATION_FORM = "af_open_registration_form"
    const val REGISTRATION_NEXT_FORM = "af_touch_next_registration_form"
    const val OPEN_OTP_FORM = "af_open_otp_form"
    const val OTP_NEXT_FORM = "af_touch_next_otp_form"
    const val INTRODUCTION_CAPTURE_CARD_SCREEN = "af_touch_understand_capture_instruction_screen"
    const val CAPTURE_FRONT_CARD = "af_capture_front_id"
    const val CAPTURE_BACK_CARD = "af_capture_back_id"
    const val INTRODUCTION_SELFIE_SCREEN = "af_touch_understand_selfie_instruction_screen"
    const val SELFIE = "af_selfie"
    const val RECONFIRM_SCREEN = "af_open_reconfirm_screen"
    const val RECONFIRM_NEXT = "af_touch_confirm_reconfirm_screen"
    const val REGISTRATION_SCREEN = "af_open_product_registration_screen"
    const val REGISTRATION_SCREEN_NEXT = "af_touch_register_registration_screen"
    const val PASSWORD_SCREEN = "af_open_password_registration_screen"
    const val PASSWORD_SCREEN_NEXT = "af_touch_register_password_registration_screen"
    const val SUCCESS = "af_open_success_screen"
}