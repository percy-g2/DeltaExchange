package crypto.delta.exchange.openexchange.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ErrorResponse {
    @SerializedName("error")
    @Expose
    var error: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

}