package crypto.delta.exchange.openexchange.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DeleteOrderRequest {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("product_id")
    @Expose
    var productId: Int? = null

}