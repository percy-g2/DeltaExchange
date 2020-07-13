package crypto.delta.exchange.openexchange.pojo

import com.squareup.moshi.Json

class Subscribe(
    @field:Json(name = "type")
    var type: String,

    @field:Json(name = "payload")
    var payload: Payload
)

class Payload {
    @field:Json(name = "channels")
    var channels: List<Channel>? = null

    @field:Json(name = "api-key")
    var apiKey: String? = null

    @field:Json(name = "signature")
    var signature: String? = null

    @field:Json(name = "timestamp")
    var timestamp: String? = null
}


class Channel {
    @field:Json(name = "name")
    var name: String? = null

    @field:Json(name = "symbols")
    var symbols: List<String>? = null
}