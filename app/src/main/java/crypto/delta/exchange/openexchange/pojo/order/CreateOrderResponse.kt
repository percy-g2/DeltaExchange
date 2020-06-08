package crypto.delta.exchange.openexchange.pojo.order

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CreateOrderResponse {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("user_id")
    @Expose
    var userId: Int? = null

    @SerializedName("size")
    @Expose
    var size: Int? = null

    @SerializedName("unfilled_size")
    @Expose
    var unfilledSize: Int? = null

    @SerializedName("side")
    @Expose
    var side: String? = null

    @SerializedName("order_type")
    @Expose
    var orderType: String? = null

    @SerializedName("limit_price")
    @Expose
    var limitPrice: String? = null

    @SerializedName("stop_order_type")
    @Expose
    var stopOrderType: String? = null

    @SerializedName("stop_price")
    @Expose
    var stopPrice: String? = null

    @SerializedName("close_on_trigger")
    @Expose
    var closeOnTrigger: String? = null

    @SerializedName("state")
    @Expose
    var state: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("product")
    @Expose
    var product: Product? = null

}