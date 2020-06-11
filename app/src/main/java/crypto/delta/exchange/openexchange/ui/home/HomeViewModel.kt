package crypto.delta.exchange.openexchange.ui.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import crypto.delta.exchange.openexchange.api.DeltaRepository
import crypto.delta.exchange.openexchange.pojo.products.ProductsResponse

class HomeViewModel : ViewModel() {
    private var mutableLiveData: MutableLiveData<List<ProductsResponse>>? = null
    private var deltaRepository: DeltaRepository? = null

    fun init(context: Context) {
        deltaRepository = DeltaRepository.getInstance(context)
        mutableLiveData = deltaRepository!!.getProducts()
    }

    fun getProducts(): LiveData<List<ProductsResponse>>? {
        return mutableLiveData
    }
}