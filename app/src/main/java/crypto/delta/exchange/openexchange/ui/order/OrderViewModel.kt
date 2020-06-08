package crypto.delta.exchange.openexchange.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import crypto.delta.exchange.openexchange.api.DeltaExchangeSocketServiceRepository
import io.reactivex.disposables.CompositeDisposable

class OrderViewModel : ViewModel() {
    var socketService: DeltaExchangeSocketServiceRepository? = null

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

}