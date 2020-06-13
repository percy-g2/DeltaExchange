package crypto.delta.exchange.openexchange.ui.order

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import crypto.delta.exchange.openexchange.BaseViewModel
import crypto.delta.exchange.openexchange.api.DeltaRepository
import crypto.delta.exchange.openexchange.pojo.OrderBookResponse

class RecentTradesViewModel(application: Application) : BaseViewModel(application) {
    private var mutableLiveData: MutableLiveData<OrderBookResponse?>? = null
    private var deltaRepository: DeltaRepository? = null

    fun init(context: Context) {
        deltaRepository = DeltaRepository.getInstance(this.getApplication())
    }

    fun getOrderBook(productId: String): LiveData<OrderBookResponse?>? {
        mutableLiveData = deltaRepository!!.getOrderBook(productId)
        return mutableLiveData
    }
}