package crypto.delta.exchange.openexchange.ui.chart

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import crypto.delta.exchange.openexchange.api.DeltaRepository
import crypto.delta.exchange.openexchange.pojo.DeltaExchangeChartHistoryResponse

class ChartViewModel : ViewModel() {
    private var mutableLiveData: MutableLiveData<DeltaExchangeChartHistoryResponse?>? = null
    private var deltaRepository: DeltaRepository? = null

    fun init(context: Context) {
        if (mutableLiveData != null) {
            return
        }
        deltaRepository = DeltaRepository.getInstance(context)
        mutableLiveData = deltaRepository!!.getChartHistory()
    }

    fun getChartHistory(): LiveData<DeltaExchangeChartHistoryResponse?>? {
        return mutableLiveData
    }
}