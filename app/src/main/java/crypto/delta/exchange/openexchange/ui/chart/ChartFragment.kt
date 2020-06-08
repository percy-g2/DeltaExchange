package crypto.delta.exchange.openexchange.ui.chart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.liihuu.klinechart.component.Candle
import com.liihuu.klinechart.model.KLineModel
import crypto.delta.exchange.openexchange.R
import crypto.delta.exchange.openexchange.pojo.ChartData
import crypto.delta.exchange.openexchange.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_chart.*
import java.io.IOException
import java.util.concurrent.TimeUnit


class ChartFragment : BaseFragment() {

    private lateinit var chartViewModel: ChartViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        chartViewModel = ViewModelProvider(this).get(ChartViewModel::class.java)
        chartViewModel.init(requireActivity())
        chartViewModel.getChartHistory()!!.observe(viewLifecycleOwner, Observer {
            try {
                if (it != null) {
                    val responseData = it
                    var count = -1
                    val chartData = ArrayList<ChartData>()
                    for (time in responseData.t!!.indices) {
                        val data = ChartData()
                        data.time = responseData.t!![time]
                        chartData.add(data)
                    }
                    for (high in responseData.h!!.indices) {
                        chartData[high].high = responseData.h!![high]
                    }
                    for (low in responseData.l!!.indices) {
                        chartData[low].low = responseData.l!![low]
                    }
                    for (open in responseData.o!!.indices) {
                        chartData[open].open = responseData.o!![open]
                    }
                    for (close in responseData.c!!.indices) {
                        chartData[close].close = responseData.c!![close]
                    }
                    for (close in responseData.v!!.indices) {
                        chartData[close].volume = responseData.v!![close]
                    }
                    k_line_chart.candle.candleStyle = Candle.CandleStyle.SOLID
                    val klinedata = mutableListOf<KLineModel>()
                    for (s in chartData) {
                        count += 1
                        val kLineModel = KLineModel()
                        kLineModel.openPrice = s.open!!
                        kLineModel.closePrice = s.close!!
                        kLineModel.highPrice = s.high!!
                        kLineModel.lowPrice = s.low!!
                        kLineModel.volume = s.volume!!.toDouble()
                        kLineModel.timestamp = TimeUnit.SECONDS.toMillis(s.time!!)
                        klinedata.add(count, kLineModel)
                    }
                    k_line_chart.addData(klinedata)
                    requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
            chartProgressSpinner!!.visibility = View.GONE
        })
        return inflater.inflate(R.layout.fragment_chart, container, false)
    }
}
