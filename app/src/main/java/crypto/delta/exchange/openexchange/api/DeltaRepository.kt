package crypto.delta.exchange.openexchange.api

import android.content.Context
import androidx.lifecycle.MutableLiveData
import crypto.delta.exchange.openexchange.pojo.DeltaExchangeChartHistoryResponse
import crypto.delta.exchange.openexchange.pojo.OrderBookResponse
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

    fun getChartHistory(): MutableLiveData<DeltaExchangeChartHistoryResponse?> {
        val data: MutableLiveData<DeltaExchangeChartHistoryResponse?> = MutableLiveData<DeltaExchangeChartHistoryResponse?>()
        val currentTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
        deltaExchangeApiEndPoints!!.getChartHistory("BTCUSD", "30", "1590664611", currentTime.toString()).enqueue(object :
            Callback<DeltaExchangeChartHistoryResponse?> {
            override fun onResponse(
                call: Call<DeltaExchangeChartHistoryResponse?>?,
                response: Response<DeltaExchangeChartHistoryResponse?>
            ) {
                if (response.isSuccessful) {
                    data.value = response.body()
                }
            }

            override fun onFailure(call: Call<DeltaExchangeChartHistoryResponse?>?, t: Throwable?) {
                data.value = null
            }
        })
        return data
    }

    fun getOrderBook(): MutableLiveData<OrderBookResponse?> {
        val newsData: MutableLiveData<OrderBookResponse?> = MutableLiveData<OrderBookResponse?>()
        deltaExchangeApiEndPoints!!.getOrderBook("16").enqueue(object :
            Callback<OrderBookResponse?> {
            override fun onResponse(
                call: Call<OrderBookResponse?>?,
                response: Response<OrderBookResponse?>
            ) {
                if (response.isSuccessful) {
                    newsData.value = response.body()
                }
            }

            override fun onFailure(call: Call<OrderBookResponse?>?, t: Throwable?) {
                newsData.value = null
            }
        })
        return newsData
    }
}