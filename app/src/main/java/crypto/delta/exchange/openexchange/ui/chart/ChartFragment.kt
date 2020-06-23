package crypto.delta.exchange.openexchange.ui.chart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.liihuu.klinechart.component.Candle
import com.liihuu.klinechart.component.Indicator
import com.liihuu.klinechart.model.KLineModel
import crypto.delta.exchange.openexchange.R
import crypto.delta.exchange.openexchange.pojo.ChartData
import crypto.delta.exchange.openexchange.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_chart.*
import java.io.IOException
import java.util.concurrent.TimeUnit


class ChartFragment : BaseFragment() {

    private lateinit var chartViewModel: ChartViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        chartViewModel = ViewModelProvider(this).get(ChartViewModel::class.java)
        chartViewModel.init()
        return inflater.inflate(R.layout.fragment_chart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentSymbol.text = appPreferenceManager!!.currentProductSymbol!!


        when (appPreferenceManager!!.currentChartResolution!!) {
            "5" -> {
                chartResolutionTab.selectTab(chartResolutionTab.getTabAt(0))
                loadChart("5")
            }
            "15" -> {
                chartResolutionTab.selectTab(chartResolutionTab.getTabAt(1))
                loadChart("15")
            }
            "30" -> {
                chartResolutionTab.selectTab(chartResolutionTab.getTabAt(2))
                loadChart("30")
            }
            "60" -> {
                chartResolutionTab.selectTab(chartResolutionTab.getTabAt(3))
                loadChart("60")
            }
            "240" -> {
                chartResolutionTab.selectTab(chartResolutionTab.getTabAt(4))
                loadChart("240")
            }
            "360" -> {
                chartResolutionTab.selectTab(chartResolutionTab.getTabAt(5))
                loadChart("360")
            }
            "D" -> {
                chartResolutionTab.selectTab(chartResolutionTab.getTabAt(6))
                loadChart("D")
            }
            "1W" -> {
                chartResolutionTab.selectTab(chartResolutionTab.getTabAt(7))
                loadChart("1W")
            }
            "30D" -> {
                chartResolutionTab.selectTab(chartResolutionTab.getTabAt(8))
                loadChart("30D")
            }
        }


        chartResolutionTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when {
                    tab!!.text.toString() == resources.getString(R.string.fiveMin) -> {
                        loadChart("5")
                        appPreferenceManager!!.setChartResolution("5")
                    }
                    tab.text.toString() == resources.getString(R.string.fifteenMins) -> {
                        loadChart("15")
                        appPreferenceManager!!.setChartResolution("15")
                    }
                    tab.text.toString() == resources.getString(R.string.thirtyMins) -> {
                        loadChart("30")
                        appPreferenceManager!!.setChartResolution("30")
                    }
                    tab.text.toString() == resources.getString(R.string.oneHour) -> {
                        loadChart("60")
                        appPreferenceManager!!.setChartResolution("60")
                    }
                    tab.text.toString() == resources.getString(R.string.fourHours) -> {
                        loadChart("240")
                        appPreferenceManager!!.setChartResolution("240")
                    }
                    tab.text.toString() == resources.getString(R.string.sixHours) -> {
                        loadChart("360")
                        appPreferenceManager!!.setChartResolution("360")
                    }
                    tab.text.toString() == resources.getString(R.string.oneDay) -> {
                        loadChart("D")
                        appPreferenceManager!!.setChartResolution("D")
                    }
                    tab.text.toString() == resources.getString(R.string.oneWeek) -> {
                        loadChart("1W")
                        appPreferenceManager!!.setChartResolution("1W")
                    }
                    tab.text.toString() == resources.getString(R.string.oneMonth) -> {
                        loadChart("30D")
                        appPreferenceManager!!.setChartResolution("30D")
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }
        })
    }

    private fun loadChart(resolution: String) {
        progressSpinner!!.visibility = View.VISIBLE
        val currentTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
        chartViewModel.getChartHistory(
            resolution,
            appPreferenceManager!!.currentProductSymbol!!,
            "1105261585",
            currentTime.toString()
        )!!.observe(viewLifecycleOwner, Observer {
            try {
                if (it != null) {
                    k_line_chart.clearDataList()
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
                    k_line_chart.setSubIndicatorType(Indicator.Type.NO)
                    k_line_chart.setMainIndicatorType(Indicator.Type.NO)
                    requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
            progressSpinner!!.visibility = View.GONE
        })
    }
}
