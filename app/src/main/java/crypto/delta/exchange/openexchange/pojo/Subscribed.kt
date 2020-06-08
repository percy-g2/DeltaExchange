package crypto.delta.exchange.openexchange.pojo

import com.squareup.moshi.Json

class Subscribed {
    @Json(name = "type")
    var type: String? = null

    @Json(name = "channels")
    var channels: List<Channel>? = null
}