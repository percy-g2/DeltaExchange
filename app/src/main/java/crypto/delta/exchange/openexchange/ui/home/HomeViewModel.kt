package crypto.delta.exchange.openexchange.ui.home

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import crypto.delta.exchange.openexchange.BaseViewModel
import crypto.delta.exchange.openexchange.api.DeltaRepository
import crypto.delta.exchange.openexchange.pojo.products.ProductsResponse
import io.reactivex.disposables.CompositeDisposable

class HomeViewModel(application: Application) : BaseViewModel(application) {
    private var mutableLiveData: MutableLiveData<List<ProductsResponse>>? = null
    private var deltaRepository: DeltaRepository? = null
    private val disposables: CompositeDisposable = CompositeDisposable()

    fun init() {
        deltaRepository = DeltaRepository.getInstance(this.getApplication())
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