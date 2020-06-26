package crypto.delta.exchange.openexchange.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class WalletResponse {
    @SerializedName("balance")
    @Expose
    var balance: String? = null

    @SerializedName("order_margin")
    @Expose
    var orderMargin: String? = null

    @SerializedName("position_margin")
    @Expose
    var positionMargin: String? = null

    @SerializedName("commission")
    @Expose
    var commission: String? = null

    @SerializedName("available_balance")
    @Expose
    var availableBalance: String? = null

    @SerializedName("trading_fee_credit")
    @Expose
    var tradingFeeCredit: String? = null

    @SerializedName("asset")
    @Expose
    var asset: Asset? = null

    @SerializedName("interest_credit")
    @Expose
    var interestCredit: String? = null

}