package crypto.delta.exchange.openexchange.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Asset {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("symbol")
    @Expose
    var symbol: String? = null

    @SerializedName("precision")
    @Expose
    var precision: Int? = null

    @SerializedName("minimum_precision")
    @Expose
    var minimumPrecision: Int? = null

    @SerializedName("sort_priority")
    @Expose
    var sortPriority: Any? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("withdrawal_status")
    @Expose
    var withdrawalStatus: Any? = null

    @SerializedName("interest_slabs")
    @Expose
    var interestSlabs: Any? = null

    @SerializedName("interest_credit")
    @Expose
    var interestCredit: Boolean? = null

}