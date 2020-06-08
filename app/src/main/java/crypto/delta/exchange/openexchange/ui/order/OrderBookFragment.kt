package crypto.delta.exchange.openexchange.ui.order

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle
import com.tinder.scarlet.messageadapter.moshi.MoshiMessageAdapter
import com.tinder.scarlet.retry.ExponentialWithJitterBackoffStrategy
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import crypto.delta.exchange.openexchange.R
import crypto.delta.exchange.openexchange.adapter.OrderBookAdapter
import crypto.delta.exchange.openexchange.api.DeltaExchangeSocketServiceRepository
import crypto.delta.exchange.openexchange.pojo.Channel
import crypto.delta.exchange.openexchange.pojo.DeltaExchangeL2OrderBookResponse
import crypto.delta.exchange.openexchange.pojo.Payload
import crypto.delta.exchange.openexchange.pojo.Subscribe
import crypto.delta.exchange.openexchange.ui.base.BaseFragment
import crypto.delta.exchange.openexchange.utils.Native
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_orders.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class OrderBookFragment: BaseFragment() {
    private val disposables: CompositeDisposable = CompositeDisposable()
    private var orderBookAdapter: OrderBookAdapter? = null
    private var orderBookList: DeltaExchangeL2OrderBookResponse = DeltaExchangeL2OrderBookResponse()
    private var layoutManager: LinearLayoutManager? = null
    private var deltaExchangeSocketServiceRepository: DeltaExchangeSocketServiceRepository? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        lifecycle.let {
            val lifecycle = AndroidLifecycle.ofLifecycleOwnerForeground(requireActivity().application, viewLifecycleOwner)
            val backoffStrategy = ExponentialWithJitterBackoffStrategy(5000, 5000)
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()

            deltaExchangeSocketServiceRepository = Scarlet.Builder()
                .webSocketFactory(okHttpClient.newWebSocketFactory(Native.deltaExchangeBaseWebSocketUrl))
                .addMessageAdapterFactory(MoshiMessageAdapter.Factory())
                .addStreamAdapterFactory(RxJava2StreamAdapterFactory())
                .backoffStrategy(backoffStrategy)
           //     .lifecycle(lifecycle)
                .build()
                .create()


            deltaExchangeSocketServiceRepository!!.observeWebSocketEvent()
                .subscribe({
                    // If connection is open
                    if (it is WebSocket.Event.OnConnectionOpened<*>) {

                        // val channel = Channel()
                        //  channel.name = "ticker"
                        // channel.symbols = arrayListOf("BTCUSD")

                        val channel2 = Channel()
                        channel2.name = "l2_orderbook"
                        channel2.symbols = arrayListOf("BTCUSD")

                        val payload = Payload()
                        payload.channels = arrayListOf(channel2)
                        val subscribe = Subscribe(
                            "subscribe",
                            payload
                        )

                        // Subscribe to Bitcoin ticker
                        deltaExchangeSocketServiceRepository!!.sendSubscribe(subscribe)
                    }

                    if (it is WebSocket.Event.OnConnectionFailed) {
                        Log.e(
                            OrderFragment::class.java.simpleName,
                            "OnConnectionFailed ${it.throwable.printStackTrace()}"
                        )
                    }
                    if (it is WebSocket.Event.OnConnectionClosed) {
                        Log.e(
                            OrderFragment::class.java.simpleName,
                            "OnConnectionClosed ${it.shutdownReason.reason}"
                        )
                    }
                    if (it is WebSocket.Event.OnMessageReceived) {
                        /*Log.e(
                            OrderFragment::class.java.simpleName,
                            "OnMessageReceived ${it.message}"
                        )*/
                    }
                    if (it is WebSocket.Event.OnConnectionClosing) {
                        Log.e(
                            OrderFragment::class.java.simpleName,
                            "OnConnectionClosing ${it.shutdownReason.reason}"
                        )
                    }
                }, { error ->
                    Log.e(
                        OrderFragment::class.java.simpleName,
                        "Error while observing socket ${error.cause}"
                    )
                }).addTo(disposables)

            deltaExchangeSocketServiceRepository!!.observeOrderBook()
                .subscribe({
                    CoroutineScope(Dispatchers.Main).launch {
                        withContext(Dispatchers.Main) {
                            if (!it.toString().contains("subscription".toRegex())) {
                                val data = it.toString().replace("size", "d_size")
                                val gson = Gson()
                                val response =
                                    gson.fromJson(
                                        data,
                                        DeltaExchangeL2OrderBookResponse::class.java
                                    )
                                response.buy = response.buy!!.slice(IntRange(1, 10)).sortedBy {
                                    it.d_size
                                }
                                response.sell = response.sell!!.slice(IntRange(1, 10)).sortedBy {
                                    it.d_size
                                }
                                orderBookAdapter!!.updateOrderBook(response)
                                if (layoutManager!!.findFirstVisibleItemPosition() == 0) {
                                    layoutManager!!.scrollToPositionWithOffset(0, 0)
                                }
                            }
                        }
                    }
                }, { error ->
                    Log.e(
                        OrderFragment::class.java.simpleName,
                        "Error while observing ticker ${error.cause}"
                    )
                }).addTo(disposables)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutManager = LinearLayoutManager(requireContext())
        list.layoutManager = layoutManager
        orderBookAdapter = OrderBookAdapter(orderBookList)
        list.adapter = orderBookAdapter
        list.itemAnimator = null
        list.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        val channel2 = Channel()
        channel2.name = "l2_orderbook"
        channel2.symbols = arrayListOf()

        val payload = Payload()
        payload.channels = arrayListOf(channel2)
        val subscribe = Subscribe(
            "unsubscribe",
            payload
        )

        // Subscribe to Bitcoin ticker
        deltaExchangeSocketServiceRepository!!.sendUnSubscribe(subscribe)
        disposables.clear()
    }
}