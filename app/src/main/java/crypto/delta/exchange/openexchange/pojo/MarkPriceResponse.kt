package crypto.delta.exchange.openexchange.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MarkPriceResponse {
    @SerializedName("symbol")
    @Expose
    var symbol: String? = null

    @SerializedName("product_id")
    @Expose
    var productId: Int? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("price")
    @Expose
    var price: String? = null

    @SerializedName("annualized_basis")
    @Expose
    var annualizedBasis: Double? = null

    @SerializedName("timestamp")
    @Expose
    var timestamp: Long? = null

}