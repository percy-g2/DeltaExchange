package crypto.delta.exchange.openexchange.pojo

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Sell {
    @SerializedName("limit_price")
    @Expose
    var limitPrice: Double? = null

    @SerializedName("d_size")
    @Expose
    var d_size: Double? = null

}