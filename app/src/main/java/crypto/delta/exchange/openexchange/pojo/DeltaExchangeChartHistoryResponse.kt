package crypto.delta.exchange.openexchange.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DeltaExchangeChartHistoryResponse {
    @SerializedName("s")
    @Expose
    var s: String? = null

    @SerializedName("t")
    @Expose
    var t: List<Long>? = null

    @SerializedName("o")
    @Expose
    var o: List<Double>? = null

    @SerializedName("h")
    @Expose
    var h: List<Double>? = null

    @SerializedName("l")
    @Expose
    var l: List<Double>? = null

    @SerializedName("c")
    @Expose
    var c: List<Double>? = null

    @SerializedName("v")
    @Expose
    var v: List<Int>? = null

}