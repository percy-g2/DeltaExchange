package crypto.delta.exchange.openexchange.ui.order

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import crypto.delta.exchange.openexchange.api.DeltaRepository
import crypto.delta.exchange.openexchange.pojo.OrderBookResponse

class RecentTradesViewModel : ViewModel() {
    private var mutableLiveData: MutableLiveData<OrderBookResponse?>? = null
    private var deltaRepository: DeltaRepository? = null

    fun init(context: Context) {
        deltaRepository = DeltaRepository.getInstance(context)
        mutableLiveData = deltaRepository!!.getOrderBook()
    }

    fun getOrderBook(): LiveData<OrderBookResponse?>? {
        return mutableLiveData
    }
}