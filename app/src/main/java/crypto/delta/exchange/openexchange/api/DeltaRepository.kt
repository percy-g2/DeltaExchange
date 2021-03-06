package crypto.delta.exchange.openexchange.api

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import crypto.delta.exchange.openexchange.BaseViewModel
import crypto.delta.exchange.openexchange.pojo.DeleteOrderRequest
import crypto.delta.exchange.openexchange.pojo.DeltaExchangeChartHistoryResponse
import crypto.delta.exchange.openexchange.pojo.DeltaExchangeTickerResponse
import crypto.delta.exchange.openexchange.pojo.OrderBookResponse
import crypto.delta.exchange.openexchange.pojo.order.CreateOrderResponse
import crypto.delta.exchange.openexchange.pojo.position.OpenPositionResponse
import crypto.delta.exchange.openexchange.pojo.products.ProductsResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DeltaRepository(application: Application) : BaseViewModel(application) {
    companion object {
        private var deltaRepository: DeltaRepository? = null
        private var context: Context? = null

        fun getInstance(application: Application): DeltaRepository? {
            this.context = application.applicationContext
            if (deltaRepository == null) {
                deltaRepository = DeltaRepository(application)
            }
            return deltaRepository
        }
    }

    private var deltaExchangeApiEndPoints: DeltaExchangeApiEndPoints? = null

    init {
        deltaExchangeApiEndPoints = DeltaApiClient().getClient(context)!!
            .create(DeltaExchangeApiEndPoints::class.java)
    }

    fun getChartHistory(
        resolution: String,
        symbol: String,
        strFrom: String,
        strTo: String
    ): MutableLiveData<DeltaExchangeChartHistoryResponse?> {
        val data: MutableLiveData<DeltaExchangeChartHistoryResponse?> =
            MutableLiveData()
        deltaExchangeApiEndPoints!!.getChartHistory(symbol, resolution, strFrom, strTo)
            .enqueue(object :
                Callback<DeltaExchangeChartHistoryResponse?> {
                override fun onResponse(
                    call: Call<DeltaExchangeChartHistoryResponse?>?,
                    response: Response<DeltaExchangeChartHistoryResponse?>
                ) {
                    if (response.isSuccessful) {
                        data.value = response.body()
                    } else {
                        data.value = null
                    }
                }

                override fun onFailure(
                    call: Call<DeltaExchangeChartHistoryResponse?>?,
                    t: Throwable?
                ) {
                    data.value = null
                }
            })
        return data
    }

    fun getOrders(apiKey: String, timestamp: String, signature: String, productId: String, state: String): MutableLiveData<List<CreateOrderResponse>?> {
        val data: MutableLiveData<List<CreateOrderResponse>?> = MutableLiveData()
        deltaExchangeApiEndPoints!!.getOrders(apiKey, timestamp, signature, state).enqueue(object :
            Callback<List<CreateOrderResponse>?> {
            override fun onResponse(
                call: Call<List<CreateOrderResponse>?>?,
                response: Response<List<CreateOrderResponse>?>
            ) {
                if (response.isSuccessful) {
                    data.value = response.body()
                } else {
                    data.value = null
                }
            }

            override fun onFailure(call: Call<List<CreateOrderResponse>?>?, t: Throwable?) {
                data.value = null
            }
        })
        return data
    }

    fun getStopOrders(
        apiKey: String,
        timestamp: String,
        signature: String,
        stopOrderType: String,
        state: String
    ): MutableLiveData<List<CreateOrderResponse>?> {
        val data: MutableLiveData<List<CreateOrderResponse>?> = MutableLiveData()
        deltaExchangeApiEndPoints!!.getStopOrders(
            apiKey,
            timestamp,
            signature,
            state,
            stopOrderType
        ).enqueue(object :
            Callback<List<CreateOrderResponse>?> {
            override fun onResponse(
                call: Call<List<CreateOrderResponse>?>?,
                response: Response<List<CreateOrderResponse>?>
            ) {
                if (response.isSuccessful) {
                    data.value = response.body()
                } else {
                    data.value = null
                }
            }

            override fun onFailure(call: Call<List<CreateOrderResponse>?>?, t: Throwable?) {
                data.value = null
            }
        })
        return data
    }

    fun getOpenPositions(
        apiKey: String,
        timestamp: String,
        signature: String
    ): MutableLiveData<List<OpenPositionResponse>?> {
        val data: MutableLiveData<List<OpenPositionResponse>?> =
            MutableLiveData()
        deltaExchangeApiEndPoints!!.getOpenPositions(apiKey, timestamp, signature).enqueue(object :
            Callback<List<OpenPositionResponse>?> {
            override fun onResponse(
                call: Call<List<OpenPositionResponse>?>?,
                response: Response<List<OpenPositionResponse>?>
            ) {
                if (response.isSuccessful) {
                    data.value = response.body()
                } else {
                    data.value = null
                }
            }

            override fun onFailure(call: Call<List<OpenPositionResponse>?>?, t: Throwable?) {
                data.value = null
            }
        })
        return data
    }

    fun getOrderBook(productId: String): MutableLiveData<OrderBookResponse?> {
        val data: MutableLiveData<OrderBookResponse?> = MutableLiveData()
        deltaExchangeApiEndPoints!!.getOrderBook(productId).enqueue(object :
            Callback<OrderBookResponse?> {
            override fun onResponse(
                call: Call<OrderBookResponse?>?,
                response: Response<OrderBookResponse?>
            ) {
                if (response.isSuccessful) {
                    data.value = response.body()
                } else {
                    data.value = null
                }
            }

            override fun onFailure(call: Call<OrderBookResponse?>?, t: Throwable?) {
                data.value = null
            }
        })
        return data
    }

    fun getProducts(disposables: CompositeDisposable): MutableLiveData<List<ProductsResponse>> {
        val data: MutableLiveData<List<ProductsResponse>> =
            MutableLiveData()
        deltaExchangeApiEndPoints!!.getProducts()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<List<ProductsResponse>>() {
                override fun onComplete() {
                }

                override fun onNext(response: List<ProductsResponse>) {
                    data.value = response
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    data.value = null
                }

            }).addTo(disposables)
        return data
    }

    fun getProductsData(symbol: String): MutableLiveData<DeltaExchangeTickerResponse>? {
        val data: MutableLiveData<DeltaExchangeTickerResponse> =
            MutableLiveData()
        deltaExchangeApiEndPoints!!.getTickers24Hrs(symbol)
            .enqueue(object :
                Callback<DeltaExchangeTickerResponse?> {
                override fun onResponse(
                    call: Call<DeltaExchangeTickerResponse?>?,
                    response: Response<DeltaExchangeTickerResponse?>
                ) {
                    if (response.isSuccessful) {
                        data.value = response.body()
                    } else {
                        data.value = null
                    }
                }

                override fun onFailure(call: Call<DeltaExchangeTickerResponse?>?, t: Throwable?) {
                    data.value = null
                }
            })
        return data
    }

    fun getWallet(
        apiKey: String,
        timestamp: String,
        signature: String
    ): MutableLiveData<Response<ResponseBody?>>? {
        val data: MutableLiveData<Response<ResponseBody?>> =
            MutableLiveData()
        deltaExchangeApiEndPoints!!.getWallet(apiKey, timestamp, signature)
            .enqueue(object :
                Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>?,
                    response: Response<ResponseBody?>
                ) {

                    data.value = response
                }

                override fun onFailure(call: Call<ResponseBody?>?, t: Throwable?) {
                    data.value = null
                }
            })
        return data
    }

    fun cancelOrder(
        apiKey: String,
        timestamp: String,
        signature: String,
        deleteOrderRequest: DeleteOrderRequest
    ): MutableLiveData<Response<ResponseBody?>>? {
        val data: MutableLiveData<Response<ResponseBody?>> =
            MutableLiveData()
        deltaExchangeApiEndPoints!!.cancelOrder(apiKey, timestamp, signature, deleteOrderRequest)
            .enqueue(object :
                Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>?,
                    response: Response<ResponseBody?>
                ) {

                    data.value = response
                }

                override fun onFailure(call: Call<ResponseBody?>?, t: Throwable?) {
                    data.value = null
                }
            })
        return data
    }
}