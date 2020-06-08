package crypto.delta.exchange.openexchange.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OrderBookResponse {
    @SerializedName("buy_book")
    @Expose
    var buyBook: List<BuyBook>? = null

    @SerializedName("funding_rate")
    @Expose
    var fundingRate: String? = null

    @SerializedName("last_trade")
    @Expose
    var lastTrade: LastTrade? = null

    @SerializedName("mark_price")
    @Expose
    var markPrice: String? = null

    @SerializedName("product_id")
    @Expose
    var productId: Int? = null

    @SerializedName("recent_trades")
    @Expose
    var recentTrades: ArrayList<RecentTrade>? = null

    @SerializedName("sell_book")
    @Expose
    var sellBook: List<SellBook>? = null

    @SerializedName("spot_price")
    @Expose
    var spotPrice: String? = null

    @SerializedName("symbol")
    @Expose
    var symbol: String? = null

}