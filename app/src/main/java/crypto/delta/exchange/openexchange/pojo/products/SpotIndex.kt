package crypto.delta.exchange.openexchange.pojo.products

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SpotIndex {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("symbol")
    @Expose
    var symbol: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("constituent_exchanges")
    @Expose
    var constituentExchanges: List<ConstituentExchange>? = null

    @SerializedName("price_method")
    @Expose
    var priceMethod: String? = null

    @SerializedName("health_interval")
    @Expose
    var healthInterval: Int? = null

    @SerializedName("impact_size")
    @Expose
    var impactSize: String? = null

    @SerializedName("is_composite")
    @Expose
    var isComposite: Boolean? = null

    @SerializedName("constituent_indices")
    @Expose
    var constituentIndices: Any? = null

    @SerializedName("index_type")
    @Expose
    var indexType: String? = null

    @SerializedName("underlying_asset")
    @Expose
    var underlyingAsset: UnderlyingAsset_? = null

    @SerializedName("quoting_asset")
    @Expose
    var quotingAsset: QuotingAsset_? = null

}