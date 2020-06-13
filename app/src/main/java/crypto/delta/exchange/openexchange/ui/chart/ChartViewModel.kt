package crypto.delta.exchange.openexchange.ui.chart

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import crypto.delta.exchange.openexchange.BaseViewModel
import crypto.delta.exchange.openexchange.api.DeltaRepository
import crypto.delta.exchange.openexchange.pojo.DeltaExchangeChartHistoryResponse

class ChartViewModel(application: Application) : BaseViewModel(application) {
    private var mutableLiveData: MutableLiveData<DeltaExchangeChartHistoryResponse?>? = null
    private var deltaRepository: DeltaRepository? = null

    fun init(context: Context) {
        if (mutableLiveData != null) {
            return
        }
        deltaRepository = DeltaRepository.getInstance(this.getApplication())
    }

    fun getChartHistory(resolution: String, symbol: String, strFrom: String, strTo: String): LiveData<DeltaExchangeChartHistoryResponse?>? {
        mutableLiveData = deltaRepository!!.getChartHistory(resolution, symbol, strFrom, strTo)
        return mutableLiveData
    }
}