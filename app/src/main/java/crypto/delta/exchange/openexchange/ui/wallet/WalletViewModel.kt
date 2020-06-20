package crypto.delta.exchange.openexchange.ui.wallet

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import crypto.delta.exchange.openexchange.BaseViewModel
import crypto.delta.exchange.openexchange.api.DeltaRepository
import okhttp3.ResponseBody
import retrofit2.Response

class WalletViewModel(application: Application) : BaseViewModel(application) {
    private var mutableLiveData: MutableLiveData<Response<ResponseBody?>>? = null
    private var deltaRepository: DeltaRepository? = null

    fun init() {
        deltaRepository = DeltaRepository.getInstance(this.getApplication())
    }

    fun getWallet(
        apiKey: String,
        timestamp: String,
        signature: String
    ): LiveData<Response<ResponseBody?>>? {
        mutableLiveData = deltaRepository!!.getWallet(apiKey, timestamp, signature)
        return mutableLiveData
    }

}