package com.pvcombank.sdk.model

object Constants {
	const val TIME_FORMAT = "YYYY-MM-DD"
	
	//region Auth
	const val GRANT_TYPE_REFRESH = "refresh_token"
	const val GRANT_TYPE_CODE = "authorization_code"
	const val GRANT_TYPE_PASSWORD = "password"
	//endregion Auth
	
	const val BASE_URL = "https://connect.apps.ocp4-dev03.pvcomtestocp.com/"
	const val BASE_URL_OTHER = "https://iuhfhsds3h.execute-api.ap-southeast-1.amazonaws.com/"
	const val REDIRECT_URL = "https://10.0.15.60/connect"
	const val url = "https://connect.apps.ocp4-dev03.pvcomtestocp.com/auth/realms/pvcombank/protocol/openid-connect/auth?client_id=vietsens-sdk&state=589i2mgvijs&redirect_uri=$REDIRECT_URL&scope=openid&response_type=code"
	//https://connect.apps.ocp4-dev03.pvcomtestocp.com/auth/realms/pvcombank/protocol/openid-connect/auth?client_id=vietsens-sdk&state=589i2mgvijs&redirect_uri=https://10.0.15.60/connect&scope=openid&response_type=code
	var TOKEN: String = ""
	
	//region http code
	const val CODE_SUCCESS = "200"
	val INLINE_ALERT_CODE = hashMapOf(
		21 to "Thẻ khóa vĩnh viễn do KH yêu cầu ngưng sử dụng thẻ",
		20 to "Thẻ tạm khóa theo yêu cầu của KH",
		19 to "Nghi ngờ lộ thông tin thẻ",
		14 to "Nghi ngờ lộ thông tin thẻ",
		12 to "Thẻ quá 180 ngày không kích hoạt",
		6 to "Thẻ báo mất cắp/thất lạc",
		15 to "Thẻ quá hạn từ 30 đến dưới 90 ngày",
		25 to "Khóa thẻ cũ sau khi thực hiện PHL thẻ mới"
	)
	val TOAST_ALERT_CODE = hashMapOf(
		5 to "Thẻ hết hạn",
		1 to "Thẻ chưa kích hoạt"
	)
	//endregion http code
	
	const val data = ""
	const val secretKey = "9875cce6826dbc1fc9083c12c6d75642"
	const val iv = "053D0C386EE38077"
	const val algorithm = "AES"
	const val algorithm_generate = "PBKDF2WithHmacSHA256"
	const val transformation = "AES/CBC/PKCS7Padding"
	const val provider = "BC"
	const val file_name = "PCV@beh5pkj!ufx6dky"
	const val rpl_1 = "+"
	const val rpl_2 = "%2b"
	
	//TRUST_VISION
	const val TS_CONFIGURATION = "{" +
			"settings: {" +
			"  sdk_settings: {" +
			"    active_liveness_settings: {" +
			"      face_tracking_setting: {" +
			"        android_terminate_threshold: 0.002847," +
			"        android_warning_threshold: 0.001474," +
			"        enable: false," +
			"        ios_terminate_threshold: 0.003393," +
			"        ios_warning_threshold: 0.002176," +
			"        limit_for: all_flow," +
			"        max_interval_ms: 2000," +
			"        max_warning_time: 5" +
			"      }," +
			"      flow_interval_time_ms: 2000," +
			"      limit_time_liveness_check: {" +
			"        enable: true," +
			"        limit_time_second: 20" +
			"      }," +
			"      record_video: {" +
			"        enable: true" +
			"      }," +
			"      save_encoded_frames: {" +
			"        enable: true," +
			"        frames_interval_ms: 180" +
			"       }," +
			"      terminate_if_no_face: {" +
			"        enable: false," +
			"        max_invalid_frame: 5," +
			"        max_time_ms: 3000" +
			"      }" +
			"    }," +
			"    id_detection_settings: {" +
			"      auto_capture: { " +
			"        enable: false," +
			"        show_capture_button: true" +
			"      }," +
			"      blur_check: {" +
			"        enable: false," +
			"        threshold: 0.29" +
			"      }," +
			"      disable_capture_button_if_alert: false," +
			"      glare_check: {" +
			"        enable: true," +
			"        threshold: 0.001" +
			"      }," +
			"      id_detection: {" +
			"        enable: true" +
			"      }," +
			"      limit_time_settings: {" +
			"        enable: false," +
			"        limit_time_second: 10" +
			"      }," +
			"      save_frame_settings: {" +
			"        enable: true," +
			"        frames_interval_ms: 190," +
			"        quality_android: 80," +
			"        quality_ios: 70" +
			"      }" +
			"    }," +
			"    liveness_settings: {" +
			"      vertical_check: {" +
			"        enable: true," +
			"        threshold: 40" +
			"      }" +
			"    }" +
			"  }" +
			"}" +
			"}"
}