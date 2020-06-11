package crypto.delta.exchange.openexchange.api

import android.content.Context
import androidx.lifecycle.MutableLiveData
import crypto.delta.exchange.openexchange.pojo.DeltaExchangeChartHistoryResponse
import crypto.delta.exchange.openexchange.pojo.OrderBookResponse
import crypto.delta.exchange.openexchange.pojo.products.ProductsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class DeltaRepository {
    companion object {
        private var deltaRepository: DeltaRepository? = null
        private var context: Context? = null

        fun getInstance(context: Context): DeltaRepository? {
            this.context = context
            if (deltaRepository == null) {
                deltaRepository = DeltaRepository()
            }
            return deltaRepository
        }
    }

    private var deltaExchangeApiEndPoints: DeltaExchangeApiEndPoints? = null

    init {
        deltaExchangeApiEndPoints = DeltaApiClient().getClient(context)!!
            .create(DeltaExchangeApiEndPoints::class.java)
    }

    fun getChartHistory(resolution: String, symbol: String): MutableLiveData<DeltaExchangeChartHistoryResponse?> {
        val data: MutableLiveData<DeltaExchangeChartHistoryResponse?> = MutableLiveData<DeltaExchangeChartHistoryResponse?>()
        val currentTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
        deltaExchangeApiEndPoints!!.getChartHistory(symbol, resolution, "1105261585", currentTime.toString()).enqueue(object :
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

            override fun onFailure(call: Call<DeltaExchangeChartHistoryResponse?>?, t: Throwable?) {
                data.value = null
            }
        })
        return data
    }

    fun getOrderBook(productId: String): MutableLiveData<OrderBookResponse?> {
        val data: MutableLiveData<OrderBookResponse?> = MutableLiveData<OrderBookResponse?>()
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

    fun getProducts(): MutableLiveData<List<ProductsResponse>> {
        val data: MutableLiveData<List<ProductsResponse>> = MutableLiveData<List<ProductsResponse>>()
        deltaExchangeApiEndPoints!!.getProducts().enqueue(object :
            Callback<List<ProductsResponse>> {
            override fun onResponse(call: Call<List<ProductsResponse>>?, response: Response<List<ProductsResponse>>) {
                if (response.isSuccessful) {
                    data.value = response.body()
                } else {
                    data.value = null
                }
            }

            override fun onFailure(call: Call<List<ProductsResponse>>?, t: Throwable?) {
                data.value = null
            }
        })
        return data
    }
}