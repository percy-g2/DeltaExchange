package crypto.delta.exchange.openexchange.pojo

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class DeltaExchangeL2OrderBookResponse {
    @SerializedName("buy")
    @Expose
    var buy: List<Buy>? = ArrayList()

    @SerializedName("sell")
    @Expose
    var sell: List<Sell>? = ArrayList()

    @SerializedName("last_sequence_no")
    @Expose
    var lastSequenceNo: Long? = null

    @SerializedName("product_id")
    @Expose
    var productId: Int? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("symbol")
    @Expose
    var symbol: String? = null

    @SerializedName("timestamp")
    @Expose
    var timestamp: Long? = null
}