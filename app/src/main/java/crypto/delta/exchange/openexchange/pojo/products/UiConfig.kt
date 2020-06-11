package crypto.delta.exchange.openexchange.pojo.products

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UiConfig {
    @SerializedName("tags")
    @Expose
    var tags: List<String>? = null

    @SerializedName("sort_priority")
    @Expose
    var sortPriority: Int? = null

    @SerializedName("show_bracket_orders")
    @Expose
    var showBracketOrders: Boolean? = null

    @SerializedName("price_clubbing_values")
    @Expose
    var priceClubbingValues: List<Double>? = null

    @SerializedName("leverage_slider_values")
    @Expose
    var leverageSliderValues: List<Int>? = null

    @SerializedName("default_trading_view_candle")
    @Expose
    var defaultTradingViewCandle: String? = null

}