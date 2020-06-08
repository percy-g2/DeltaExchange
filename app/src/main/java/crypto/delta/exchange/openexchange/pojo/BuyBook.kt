package crypto.delta.exchange.openexchange.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BuyBook {
    @SerializedName("price")
    @Expose
    var price: String? = null

    @SerializedName("size")
    @Expose
    var size: Int? = null

}