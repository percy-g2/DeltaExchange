package crypto.delta.exchange.openexchange.ui.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import crypto.delta.exchange.openexchange.api.DeltaRepository
import crypto.delta.exchange.openexchange.pojo.products.ProductsResponse
import io.reactivex.disposables.CompositeDisposable

class HomeViewModel : ViewModel() {
    private var mutableLiveData: MutableLiveData<List<ProductsResponse>>? = null
    private var deltaRepository: DeltaRepository? = null
    private val disposables: CompositeDisposable = CompositeDisposable()

    fun init(context: Context) {
        deltaRepository = DeltaRepository.getInstance(context)
    }

    fun getProducts(): LiveData<List<ProductsResponse>>? {
        mutableLiveData = deltaRepository!!.getProducts(disposables)
        return mutableLiveData
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}