package crypto.delta.exchange.openexchange.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import crypto.delta.exchange.openexchange.R
import crypto.delta.exchange.openexchange.adapter.BuyOrderBookAdapter
import crypto.delta.exchange.openexchange.adapter.SellOrderBookAdapter
import crypto.delta.exchange.openexchange.pojo.*
import crypto.delta.exchange.openexchange.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_order_book.*

class OrderBookFragment : BaseFragment() {
    private lateinit var orderBookViewModel: OrderBookViewModel
    private var buyOrderBookAdapter: BuyOrderBookAdapter? = null
    private var sellOrderBookAdapter: SellOrderBookAdapter? = null
    private var buyOrderBookList: List<Buy> = ArrayList()
    private var sellOrderBookList: List<Sell> = ArrayList()
    private var buyLayoutManager: LinearLayoutManager? = null
    private var sellLayoutManager: LinearLayoutManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        orderBookViewModel = ViewModelProvider(this).get(OrderBookViewModel::class.java)
        orderBookViewModel.init()
        return inflater.inflate(R.layout.fragment_order_book, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buyLayoutManager = LinearLayoutManager(requireContext())
        sellLayoutManager = LinearLayoutManager(requireContext())
        buyOrderBookAdapter = BuyOrderBookAdapter(buyOrderBookList)
        sellOrderBookAdapter = SellOrderBookAdapter(sellOrderBookList)

        buyOrderBookRecyclerView.layoutManager = buyLayoutManager
        buyOrderBookRecyclerView.adapter = buyOrderBookAdapter
        buyOrderBookRecyclerView.itemAnimator = null
        buyOrderBookRecyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        sellOrderBookRecyclerView.layoutManager = sellLayoutManager
        sellOrderBookRecyclerView.adapter = sellOrderBookAdapter
        sellOrderBookRecyclerView.itemAnimator = null
        sellOrderBookRecyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        orderBookViewModel.observeWebSocketEvent()
        orderBookViewModel.observeOrderBook().observe(viewLifecycleOwner, Observer {
            if (null != it) {
                buyOrderBookAdapter!!.updateOrderBook(it.buy!!)
                sellOrderBookAdapter!!.updateOrderBook(it.sell!!)
                if (buyLayoutManager!!.findFirstVisibleItemPosition() == 0) {
                    buyLayoutManager!!.scrollToPositionWithOffset(0, 0)
                } else {
                    buyOrderBookRecyclerView.scrollToPosition(buyLayoutManager!!.findLastVisibleItemPosition() - 1)
                }
                if (sellLayoutManager!!.findFirstVisibleItemPosition() == 0) {
                    sellLayoutManager!!.scrollToPositionWithOffset(0, 0)
                } else {
                    sellOrderBookRecyclerView.scrollToPosition(sellLayoutManager!!.findLastVisibleItemPosition() - 1)
                }
                if (progressSpinner.visibility == View.VISIBLE) {
                    progressSpinner.visibility = View.GONE
                }
                orderBookViewModel.getRecentTrade(appPreferenceManager!!.currentProductId!!)
                    .observe(viewLifecycleOwner, Observer { recent ->
                        if (null != recent) {
                            lastPriceTxt.text = recent.recentTrades!!.first().price
                        }
                    })
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        val channel2 = Channel()
        channel2.name = "l2_orderbook"
        channel2.symbols = arrayListOf()

        val payload = Payload()
        payload.channels = arrayListOf(channel2)
        val subscribe = Subscribe(
            "unsubscribe",
            payload
        )

        // Subscribe to Bitcoin ticker
        orderBookViewModel.sendUnSubscribe(subscribe)
    }
}