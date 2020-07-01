package crypto.delta.exchange.openexchange.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle
import com.tinder.scarlet.messageadapter.moshi.MoshiMessageAdapter
import com.tinder.scarlet.retry.ExponentialWithJitterBackoffStrategy
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import crypto.delta.exchange.openexchange.BaseViewModel
import crypto.delta.exchange.openexchange.api.DeltaExchangeSocketServiceRepository
import crypto.delta.exchange.openexchange.api.DeltaRepository
import crypto.delta.exchange.openexchange.pojo.Channel
import crypto.delta.exchange.openexchange.pojo.DeltaExchangeTickerResponse
import crypto.delta.exchange.openexchange.pojo.Payload
import crypto.delta.exchange.openexchange.pojo.Subscribe
import crypto.delta.exchange.openexchange.pojo.products.ProductsResponse
import crypto.delta.exchange.openexchange.utils.AppPreferenceManager
import crypto.delta.exchange.openexchange.utils.Native
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class HomeViewModel(application: Application) : BaseViewModel(application) {
    private var mutableLiveData: MutableLiveData<List<ProductsResponse>>? = null
    private var deltaRepository: DeltaRepository? = null
    private val disposables: CompositeDisposable = CompositeDisposable()
    private var deltaExchangeSocketServiceRepository: DeltaExchangeSocketServiceRepository? = null
    private lateinit var appPreferenceManager: AppPreferenceManager
    private val backoffStrategy = ExponentialWithJitterBackoffStrategy(5000, 5000)
    private val foreground: Lifecycle = AndroidLifecycle.ofApplicationForeground(application)
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    fun init() {
        deltaRepository = DeltaRepository.getInstance(this.getApplication())
        deltaExchangeSocketServiceRepository = Scarlet.Builder()
            .webSocketFactory(okHttpClient.newWebSocketFactory(Native.deltaExchangeBaseWebSocketUrl))
            .addMessageAdapterFactory(MoshiMessageAdapter.Factory())
            .addStreamAdapterFactory(RxJava2StreamAdapterFactory())
            .backoffStrategy(backoffStrategy)
            .lifecycle(foreground)
            .build()
            .create()
    }

    fun getProducts(): LiveData<List<ProductsResponse>>? {
        mutableLiveData = deltaRepository!!.getProducts(disposables)
        return mutableLiveData
    }

    fun observeWebSocketEvent(productsResponseList: List<ProductsResponse>) {
        val data: MutableList<String> = ArrayList()
        for (product in productsResponseList) {
            data.add(product.symbol!!)
        }
        deltaExchangeSocketServiceRepository!!.observeWebSocketEvent()
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it is WebSocket.Event.OnConnectionOpened<*>) {
                    val channel2 = Channel()
                    channel2.name = "ticker"
                    channel2.symbols = data
                    val payload = Payload()
                    payload.channels = arrayListOf(channel2)
                    val subscribe = Subscribe(
                        "subscribe",
                        payload
                    )
                    // Subscribe
                    sendSubscribe(subscribe)
                }
            }, { error ->
                Log.e(
                    HomeViewModel::class.java.simpleName,
                    "Error while observing socket ${error.cause}"
                )
                error.printStackTrace()
            }).addTo(disposables)
    }

    fun observeTicker(): MutableLiveData<DeltaExchangeTickerResponse> {
        val orderBookData: MutableLiveData<DeltaExchangeTickerResponse> =
            MutableLiveData()
        deltaExchangeSocketServiceRepository!!.observeOrderBook()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ any ->
                if (!any.toString().contains("subscription".toRegex())) {
                    if (null != any) {
                        val gson = Gson()
                        val response =
                            gson.fromJson(
                                any.toString(),
                                DeltaExchangeTickerResponse::class.java
                            )
                        orderBookData.value = response
                    }
                }
            }, { error ->
                Log.e(
                    HomeViewModel::class.java.simpleName,
                    "Error while observing ticker ${error.cause}"
                )
                error.printStackTrace()
            }).addTo(disposables)
        return orderBookData
    }

    private fun sendSubscribe(action: Subscribe) {
        deltaExchangeSocketServiceRepository!!.sendSubscribe(action)
    }


    fun sendUnSubscribe(action: Subscribe) {
        deltaExchangeSocketServiceRepository!!.sendUnSubscribe(action)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}