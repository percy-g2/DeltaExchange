package crypto.delta.exchange.openexchange.api

import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import crypto.delta.exchange.openexchange.pojo.DeltaExchangeTickerResponse
import crypto.delta.exchange.openexchange.pojo.Subscribe
import crypto.delta.exchange.openexchange.pojo.Subscribed
import io.reactivex.Flowable

interface DeltaExchangeSocketServiceRepository {
    @Send
    fun sendSubscribe(action: Subscribe)

    @Send
    fun sendUnSubscribe(action: Subscribe)

    @Receive
    fun observeTicker(): Flowable<DeltaExchangeTickerResponse>

    @Receive
    fun observeOrderBook(): Flowable<Any>

    @Receive
    fun observeRecentTrades(): Flowable<Any>

    @Receive
    fun observeWebSocketEvent(): Flowable<WebSocket.Event>

    @Receive
    fun receiveSubscribed(): Flowable<Subscribed>
}