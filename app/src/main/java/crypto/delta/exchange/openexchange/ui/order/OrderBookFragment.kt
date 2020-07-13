package crypto.delta.exchange.openexchange.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import crypto.delta.exchange.openexchange.R
import crypto.delta.exchange.openexchange.adapter.BuyOrderBookAdapter
import crypto.delta.exchange.openexchange.adapter.SellOrderBookAdapter
import crypto.delta.exchange.openexchange.pojo.Buy
import crypto.delta.exchange.openexchange.pojo.Sell
import crypto.delta.exchange.openexchange.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_order_book.*
import smartdevelop.ir.eram.showcaseviewlib.GuideView
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity
import java.lang.String
import java.util.*
import kotlin.collections.ArrayList


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

        orderBookViewModel.observeMarkPrice().observe(viewLifecycleOwner, Observer {
            if (null != it) {
                markPriceValue.text = String.format(
                    Locale.ENGLISH, "%.2f", it.price!!.toDouble()
                )
            }
        })

        orderBookViewModel.observeRecentTrade().observe(viewLifecycleOwner, Observer {
            if (null != it) {
                if (it.buyerRole.equals("taker", true)) {
                    lastPriceTxt.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorBid
                        )
                    )
                    lastPriceValue.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorBid
                        )
                    )
                } else if (it.sellerRole.equals("taker", true)) {
                    lastPriceTxt.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorAsk
                        )
                    )
                    lastPriceValue.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorAsk
                        )
                    )
                }
                lastPriceValue.text = String.format(
                    Locale.ENGLISH, "%.2f", it.price!!.toDouble()
                )
            }
        })

        orderBookViewModel.getRecentTrade(appPreferenceManager!!.currentProductId!!)
            .observe(viewLifecycleOwner, Observer { recent ->
                if (null != recent) {
                    if (recent.recentTrades!!.first().buyerRole.equals("taker", true)) {
                        lastPriceTxt.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorBid
                            )
                        )
                        lastPriceValue.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorBid
                            )
                        )
                    } else if (recent.recentTrades!!.first().sellerRole.equals("taker", true)) {
                        lastPriceTxt.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorAsk
                            )
                        )
                        lastPriceValue.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorAsk
                            )
                        )
                    }
                    lastPriceValue.text = recent.recentTrades!!.first().price
                }
            })

        markPriceTxt.setOnTouchListener(View.OnTouchListener { v, motionEvent ->
            v.performClick()
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                if (motionEvent.rawX >= markPriceTxt.right) {
                    GuideView.Builder(requireContext())
                        .setTitle("Mark Price")
                        .setContentText("Price used to mark open\n positions, i.e compute\n unrealised profit/loss and trigger\n liquidation")
                        .setGravity(Gravity.center) //optional
                        .setDismissType(DismissType.anywhere) //optional - default DismissType.targetView
                        .setTargetView(markPriceValue)
                        .setContentTextSize(12) //optional
                        .setTitleTextSize(14) //optional
                        .build()
                        .show()

                    return@OnTouchListener true
                }
            }
            return@OnTouchListener false
        })

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
            }
        })
    }
}