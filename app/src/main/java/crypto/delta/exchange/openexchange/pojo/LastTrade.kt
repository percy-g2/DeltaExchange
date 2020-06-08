package crypto.delta.exchange.openexchange.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LastTrade {
    @SerializedName("buyer_role")
    @Expose
    var buyerRole: String? = null

    @SerializedName("price")
    @Expose
    var price: String? = null

    @SerializedName("seller_role")
    @Expose
    var sellerRole: String? = null

    @SerializedName("size")
    @Expose
    var size: Int? = null

    @SerializedName("symbol")
    @Expose
    var symbol: String? = null

    @SerializedName("timestamp")
    @Expose
    var timestamp: Long? = null

}