package crypto.delta.exchange.openexchange.ui.order

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import crypto.delta.exchange.openexchange.BaseViewModel
import crypto.delta.exchange.openexchange.api.DeltaRepository
import crypto.delta.exchange.openexchange.pojo.position.OpenPositionResponse

class PositionViewModel(application: Application) : BaseViewModel(application) {
    private var mutableLiveData: MutableLiveData<List<OpenPositionResponse>?>? = null
    private var deltaRepository: DeltaRepository? = null

    fun init() {
        if (deltaRepository == null) {
            deltaRepository = DeltaRepository.getInstance(this.getApplication())
        }
    }

    fun getOpenPositions(
        apiKey: String,
        timestamp: String,
        signature: String
    ): LiveData<List<OpenPositionResponse>?>? {
        mutableLiveData =
            deltaRepository!!.getOpenPositions(apiKey, timestamp, signature)
        return mutableLiveData
    }
}