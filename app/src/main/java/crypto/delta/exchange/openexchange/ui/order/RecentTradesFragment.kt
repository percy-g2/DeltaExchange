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
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_recent_trades.*

class RecentTradesFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var recentTradesViewModel: RecentTradesViewModel

    override fun onDetach() {
        super.onDetach()
        disposables.clear()
    }

    private val disposables: CompositeDisposable = CompositeDisposable()
    private var recentTradesList: ArrayList<RecentTrade>? = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        recentTradesViewModel = ViewModelProvider(this).get(RecentTradesViewModel::class.java)
        recentTradesViewModel.init(requireActivity())
        recentTradesViewModel.getOrderBook()!!.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                recentTradesList = it.recentTrades!!
                list.adapter = RecentTradesAdapter(recentTradesList!!)
            }
            chartProgressSpinner!!.visibility = View.GONE
            if (swipeLayout.isRefreshing) {
                swipeLayout.isRefreshing = false
            }
        })
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
    }

    override fun onRefresh() {
        loadRecyclerViewData()
    }

    private fun loadRecyclerViewData() {
        recentTradesViewModel = ViewModelProvider(this).get(RecentTradesViewModel::class.java)
        recentTradesViewModel.init(requireActivity())
        recentTradesList!!.clear()
        recentTradesViewModel.getOrderBook()!!.removeObservers(viewLifecycleOwner)
        chartProgressSpinner!!.visibility = View.VISIBLE
        recentTradesViewModel.getOrderBook()!!.observe(viewLifecycleOwner, Observer {
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