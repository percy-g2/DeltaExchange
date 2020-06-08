package crypto.delta.exchange.openexchange.pojo.order

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BracketOrder {
    @SerializedName("stop_loss_price")
    @Expose
    var stopLossPrice: String? = null

    @SerializedName("take_profit_price")
    @Expose
    var takeProfitPrice: String? = null

    @SerializedName("trail_amount")
    @Expose
    var trailAmount: String? = null

}