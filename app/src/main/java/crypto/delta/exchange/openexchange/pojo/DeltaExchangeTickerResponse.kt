package crypto.delta.exchange.openexchange.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DeltaExchangeTickerResponse {
    @SerializedName("symbol")
    @Expose
    var symbol: String? = null

    @SerializedName("product_id")
    @Expose
    var productId: Int? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("timestamp")
    @Expose
    var timestamp: Long? = null

    @SerializedName("open")
    @Expose
    var open: Double? = null

    @SerializedName("high")
    @Expose
    var high: Double? = null

    @SerializedName("low")
    @Expose
    var low: Double? = null

    @SerializedName("close")
    @Expose
    var close: Double? = null

    @SerializedName("volume")
    @Expose
    var volume: Int? = null

    override fun toString(): String {
        return "symbol: $symbol \n" +
                " product_id: $productId \n" +
                " type: $type \n" +
                " timestamp: $timestamp \n" +
                " open: $open \n" +
                " high: $high \n" +
                " low: $low \n" +
                " close: $close \n" +
                " volume: $volume \n"
    }
}