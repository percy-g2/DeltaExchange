package crypto.delta.exchange.openexchange.pojo.products

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UnderlyingAsset {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("symbol")
    @Expose
    var symbol: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("precision")
    @Expose
    var precision: Int? = null

    @SerializedName("minimum_precision")
    @Expose
    var minimumPrecision: Int? = null

    @SerializedName("sort_priority")
    @Expose
    var sortPriority: Int? = null

}