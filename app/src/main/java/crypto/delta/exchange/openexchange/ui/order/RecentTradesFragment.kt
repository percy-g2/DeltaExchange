package crypto.delta.exchange.openexchange.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import crypto.delta.exchange.openexchange.R
import crypto.delta.exchange.openexchange.adapter.RecentTradesAdapter
import crypto.delta.exchange.openexchange.pojo.RecentTrade
import crypto.delta.exchange.openexchange.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_recent_trades.*

class RecentTradesFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var recentTradesViewModel: RecentTradesViewModel
    private var recentTradesList: ArrayList<RecentTrade>? = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        recentTradesViewModel = ViewModelProvider(this).get(RecentTradesViewModel::class.java)
        recentTradesViewModel.init(requireContext())
        return inflater.inflate(R.layout.fragment_recent_trades, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list.layoutManager = LinearLayoutManager(requireContext())
        list.itemAnimator = null
        list.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        swipeLayout.setOnRefreshListener(this)

        recentTradesViewModel.getOrderBook(appPreferenceManager!!.currentProductId!!)!!.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                recentTradesList = it.recentTrades!!
                list.adapter = RecentTradesAdapter(recentTradesList!!)
            }
            chartProgressSpinner!!.visibility = View.GONE
            if (swipeLayout.isRefreshing) {
                swipeLayout.isRefreshing = false
            }
        })
    }

    override fun onRefresh() {
        loadRecyclerViewData()
    }

    private fun loadRecyclerViewData() {
        recentTradesList!!.clear()
        recentTradesViewModel.getOrderBook(appPreferenceManager!!.currentProductId!!)!!.removeObservers(viewLifecycleOwner)
        chartProgressSpinner!!.visibility = View.VISIBLE
        recentTradesViewModel.getOrderBook(appPreferenceManager!!.currentProductId!!)!!.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                recentTradesList = it.recentTrades!!
                list.adapter = RecentTradesAdapter(recentTradesList!!)
            }
            chartProgressSpinner!!.visibility = View.GONE
            if (swipeLayout.isRefreshing) {
                swipeLayout.isRefreshing = false
            }
        })
    }
}