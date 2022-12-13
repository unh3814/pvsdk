import com.google.gson.annotations.SerializedName

data class ResponseData<T>(
	@SerializedName("code") var code: String? = null,
	@SerializedName("message") var message: String? = null,
	@SerializedName("data") var data: T? = null,
	@SerializedName("timestamp") var timeStamp: String? = null
)