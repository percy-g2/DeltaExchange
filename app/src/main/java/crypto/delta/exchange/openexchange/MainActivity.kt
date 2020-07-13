package crypto.delta.exchange.openexchange

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.messageadapter.moshi.MoshiMessageAdapter
import com.tinder.scarlet.retry.ExponentialWithJitterBackoffStrategy
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import crypto.delta.exchange.openexchange.api.DeltaApiClient
import crypto.delta.exchange.openexchange.api.DeltaExchangeApiEndPoints
import crypto.delta.exchange.openexchange.api.DeltaExchangeSocketServiceRepository
import crypto.delta.exchange.openexchange.pojo.Channel
import crypto.delta.exchange.openexchange.pojo.Payload
import crypto.delta.exchange.openexchange.pojo.Subscribe
import crypto.delta.exchange.openexchange.pojo.products.ProductsResponse
import crypto.delta.exchange.openexchange.ui.order.OrderBookViewModel
import crypto.delta.exchange.openexchange.utils.AppPreferenceManager
import crypto.delta.exchange.openexchange.utils.KotlinUtils
import crypto.delta.exchange.openexchange.utils.Native
import crypto.delta.exchange.openexchange.utils.setupWithNavController
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private var connectivityDisposable: Disposable? = null
    private var internetDisposable: Disposable? = null
    private var alertDialog: AlertDialog? = null
    private var deltaExchangeSocketServiceRepository: DeltaExchangeSocketServiceRepository? = null
    private val backoffStrategy = ExponentialWithJitterBackoffStrategy(5000, 5000)
    private val disposables: CompositeDisposable = CompositeDisposable()
    private var appPreferenceManager: AppPreferenceManager? = null
    private var deltaExchangeApiEndPoints: DeltaExchangeApiEndPoints? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        } // Else, need to wait for onRestoreInstanceState
        deltaExchangeApiEndPoints = DeltaApiClient().getClient(this)!!
            .create(DeltaExchangeApiEndPoints::class.java)

        deltaExchangeApiEndPoints!!.getProducts()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<List<ProductsResponse>>() {
                override fun onComplete() {
                    deltaExchangeSocketServiceRepository!!.observeWebSocketEvent()
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            if (it is WebSocket.Event.OnConnectionOpened<*>) {
                                val timeStamp = KotlinUtils.generateTimeStamp()
                                val method = "GET"
                                val path = "/live"
                                val signatureData = method + timeStamp + path
                                val signature = KotlinUtils.generateSignature(
                                    signatureData,
                                    appPreferenceManager!!.apiSecret!!
                                )
                                val payload = Payload()
                                payload.apiKey = appPreferenceManager!!.apiKey
                                payload.signature = signature
                                payload.timestamp = timeStamp
                                val subscribe = Subscribe(
                                    "auth",
                                    payload
                                )
                                // Subscribe
                                val enableHeartbeat = Subscribe(
                                    "enable_heartbeat",
                                    Payload()
                                )
                                sendSubscribe(enableHeartbeat)
                                sendSubscribe(subscribe)
                            }
                            if (it is WebSocket.Event.OnMessageReceived) {
                                Log.d("OnMessageReceived", it.message.toString())
                            }
                            if (it is WebSocket.Event.OnConnectionFailed) {
                                Log.d("OnConnectionFailed", it.toString())
                            }
                            if (it is WebSocket.Event.OnConnectionClosing) {
                                Log.d("OnConnectionClosing", it.toString())
                            }
                            if (it is WebSocket.Event.OnConnectionClosed) {
                                Log.d("OnConnectionClosed", it.shutdownReason.reason)
                            }
                        }, { error ->
                            Log.e(
                                OrderBookViewModel::class.java.simpleName,
                                "Error while observing socket ${error.cause}"
                            )
                            error.printStackTrace()
                        }).addTo(disposables)
                }

                override fun onNext(response: List<ProductsResponse>) {
                    appPreferenceManager!!.setProductSymbolsList(response.map { it.symbol!! }
                        .toMutableSet())
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

            }).addTo(disposables)

        appPreferenceManager = AppPreferenceManager(this)
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
            .build()
            .create()



        observeData().observe(this, Observer {
            Log.d("data", it)
            if (it.contains("Authenticated".toRegex())) {
                val channelOrders = Channel()
                channelOrders.name = "orders"
                channelOrders.symbols = appPreferenceManager!!.productSymbolsList!!.toMutableList()
                val channelUserTrades = Channel()
                channelUserTrades.name = "user_trades"
                channelUserTrades.symbols =
                    appPreferenceManager!!.productSymbolsList!!.toMutableList()
                val payload = Payload()
                payload.channels = arrayListOf(channelOrders, channelUserTrades)
                val subscribe = Subscribe(
                    "subscribe",
                    payload
                )
                deltaExchangeSocketServiceRepository!!.sendSubscribe(subscribe)
            }
        })
    }

    private fun observeData(): MutableLiveData<String> {
        val orderBookData: MutableLiveData<String> =
            MutableLiveData()
        deltaExchangeSocketServiceRepository!!.observeOrderBook()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ any ->

                orderBookData.value = any.toString()

            }, { error ->
                Log.e(
                    OrderBookViewModel::class.java.simpleName,
                    "Error while observing ticker ${error.cause}"
                )
                error.printStackTrace()
            }).addTo(disposables)
        return orderBookData
    }

    private fun sendSubscribe(action: Subscribe) {
        deltaExchangeSocketServiceRepository!!.sendSubscribe(action)
    }


    private fun sendUnSubscribe(action: Subscribe) {
        deltaExchangeSocketServiceRepository!!.sendUnSubscribe(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        val channelOrders = Channel()
        channelOrders.name = "orders"
        channelOrders.symbols = arrayListOf()
        val channelUserTrades = Channel()
        channelUserTrades.name = "user_trades"
        channelUserTrades.symbols = arrayListOf()

        val payload = Payload()
        payload.channels = arrayListOf(channelOrders, channelUserTrades)
        val unsubscribe = Subscribe(
            "unsubscribe",
            payload
        )
        sendUnSubscribe(unsubscribe)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigationBar()
    }

    /**
     * Called on first creation and when restoring state.
     */
    private fun setupBottomNavigationBar() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)

        val menuView =
            bottomNavigationView.getChildAt(0) as BottomNavigationMenuView
        for (i in 0 until menuView.childCount) {
            val iconView: View =
                menuView.getChildAt(i).findViewById(androidx.navigation.ui.R.id.icon)
            val layoutParams: ViewGroup.LayoutParams = iconView.layoutParams
            val displayMetrics = resources.displayMetrics
            // If it is my special menu item change the size, otherwise take other size
            if (i == 2) {
                // set your height here
                layoutParams.height = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    45f,
                    displayMetrics
                ).toInt()
                // set your width here
                layoutParams.width = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    45f,
                    displayMetrics
                ).toInt()
            } else {
                // set your height here
                layoutParams.height = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    25f,
                    displayMetrics
                ).toInt()
                // set your width here
                layoutParams.width = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    25f,
                    displayMetrics
                ).toInt()
            }
            iconView.layoutParams = layoutParams
        }

        val navGraphIds =
            listOf(
                R.navigation.home,
                R.navigation.chart,
                R.navigation.order,
                R.navigation.wallet,
                R.navigation.settings
            )

        // Setup the bottom navigation view with a list of navigation graphs
        bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_fragment,
            intent = intent
        )
    }

    override fun onResume() {
        super.onResume()

        connectivityDisposable = ReactiveNetwork.observeNetworkConnectivity(applicationContext)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { connectivity ->
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, connectivity.toString())
                    val state = connectivity.state()
                    val name = connectivity.typeName()
                    Log.i(TAG, String.format("state: %s, typeName: %s", state, name))
                }
            }

        internetDisposable = ReactiveNetwork.observeInternetConnectivity()
            .onErrorResumeNext(Observable.empty())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { isConnectedToInternet ->
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.Main) {
                        if (isConnectedToInternet) {
                            if (null != alertDialog) {
                                if (alertDialog!!.isShowing) {
                                    alertDialog!!.dismiss()
                                }
                            }
                        } else {
                            alertDialog =
                                AlertDialog.Builder(this@MainActivity, R.style.AlertDialogStyle)
                                    .setTitle("No Internet!!")
                                    .setMessage("Check your internet connectivity!")
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setView(R.layout.progress_bar)
                                    .setCancelable(false)
                                    .show()
                        }
                    }
                }
            }
    }

    override fun onPause() {
        super.onPause()
        safelyDispose(connectivityDisposable)
        safelyDispose(internetDisposable)
    }

    private fun safelyDispose(disposable: Disposable?) {
        if (disposable != null && !disposable.isDisposed) {
            disposable.dispose()
        }
    }

    companion object {
        private const val TAG = "ReactiveNetwork"
    }
}
