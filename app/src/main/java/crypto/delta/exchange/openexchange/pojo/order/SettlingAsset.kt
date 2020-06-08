package crypto.delta.exchange.openexchange.pojo.order

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SettlingAsset {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("symbol")
    @Expose
    var symbol: String? = null

    @SerializedName("precision")
    @Expose
    var precision: Int? = null

}