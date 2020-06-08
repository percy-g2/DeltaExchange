package crypto.delta.exchange.openexchange.pojo

import com.squareup.moshi.Json

class Subscribe(
    @Json(name = "type")
    var type: String,

    @Json(name = "payload")
    var payload: Payload
)

class Payload {
    @Json(name = "channels")
    var channels: List<Channel>? = null
}


class Channel {
    @Json(name = "name")
    var name: String? = null

    @Json(name = "symbols")
    var symbols: List<String>? = null
}