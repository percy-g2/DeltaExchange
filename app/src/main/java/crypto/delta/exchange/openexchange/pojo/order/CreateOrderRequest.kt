package crypto.delta.exchange.openexchange.pojo.order

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CreateOrderRequest {
    @SerializedName("product_id")
    @Expose
    var productId: Int? = null

    @SerializedName("limit_price")
    @Expose
    var limitPrice: String? = null

    @SerializedName("size")
    @Expose
    var size: Int? = null

    @SerializedName("side")
    @Expose
    var side: Side? = null

    @SerializedName("order_type")
    @Expose
    var orderType: OrderType? = null

    @SerializedName("time_in_force")
    @Expose
    var timeInForce: TimeInForce? = null

    @SerializedName("post_only")
    @Expose
    var postOnly: String? = null

    @SerializedName("reduce_only")
    @Expose
    var reduceOnly: String? = null

    @SerializedName("bracket_order")
    @Expose
    var bracketOrder: BracketOrder? = null

    enum class OrderType {
        limit_order, market_order
    }

    enum class Side {
        buy, sell
    }

    enum class TimeInForce {
        gtc, ioc, fok
    }
}