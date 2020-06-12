package crypto.delta.exchange.openexchange.pojo.products

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ConstituentExchange {
    @SerializedName("weight")
    @Expose
    var weight: Double? = null

    @SerializedName("exchange")
    @Expose
    var exchange: String? = null

    @SerializedName("health_interval")
    @Expose
    var healthInterval: Int? = null

    @SerializedName("health_priority")
    @Expose
    var healthPriority: Int? = null

    @SerializedName("toSym")
    @Expose
    var toSym: String? = null

    @SerializedName("fromSym")
    @Expose
    var fromSym: String? = null

}