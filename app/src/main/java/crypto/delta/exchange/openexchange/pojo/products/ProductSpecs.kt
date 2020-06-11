package crypto.delta.exchange.openexchange.pojo.products

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ProductSpecs {
    @SerializedName("fixed_ir_index")
    @Expose
    var fixedIrIndex: String? = null

    @SerializedName("floating_ir_index")
    @Expose
    var floatingIrIndex: String? = null

    @SerializedName("floating_rate_max")
    @Expose
    var floatingRateMax: String? = null

    @SerializedName("floating_rate_min")
    @Expose
    var floatingRateMin: String? = null

    @SerializedName("rate_exchange_interval")
    @Expose
    var rateExchangeInterval: Int? = null

}