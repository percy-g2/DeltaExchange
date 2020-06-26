package crypto.delta.exchange.openexchange.ui.order

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import crypto.delta.exchange.openexchange.BaseViewModel
import crypto.delta.exchange.openexchange.api.DeltaRepository
import crypto.delta.exchange.openexchange.pojo.DeleteOrderRequest
import crypto.delta.exchange.openexchange.pojo.order.CreateOrderResponse
import okhttp3.ResponseBody
import retrofit2.Response

class OpenOrdersViewModel(application: Application) : BaseViewModel(application) {
    private var ordersListMutableLiveData: MutableLiveData<List<CreateOrderResponse>?>? = null
    private var cancelOrderMutableLiveData: MutableLiveData<Response<ResponseBody?>>? = null
    private var deltaRepository: DeltaRepository? = null

    fun init() {
        if (deltaRepository == null) {
            deltaRepository = DeltaRepository.getInstance(this.getApplication())
        }
    }

    fun getOrders(
        apiKey: String,
        timestamp: String,
        signature: String,
        productId: String,
        state: String
    ): LiveData<List<CreateOrderResponse>?>? {
        ordersListMutableLiveData =
            deltaRepository!!.getOrders(apiKey, timestamp, signature, productId, state)
        return ordersListMutableLiveData
    }

    fun cancelOrder(
        apiKey: String,
        timestamp: String,
        signature: String,
        deleteOrderRequest: DeleteOrderRequest
    ): LiveData<Response<ResponseBody?>>? {
        cancelOrderMutableLiveData =
            deltaRepository!!.cancelOrder(apiKey, timestamp, signature, deleteOrderRequest)
        return cancelOrderMutableLiveData
    }
}