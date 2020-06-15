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
import crypto.delta.exchange.openexchange.adapter.PositionAdapter
import crypto.delta.exchange.openexchange.ui.base.BaseFragment
import crypto.delta.exchange.openexchange.utils.KotlinUtils
import kotlinx.android.synthetic.main.fragment_open_orders.*

class PositionsFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var positionViewModel: PositionViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        positionViewModel =
            ViewModelProvider(this@PositionsFragment).get(PositionViewModel::class.java)
        positionViewModel.init()
        return inflater.inflate(R.layout.fragment_positions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        openOrdersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        openOrdersRecyclerView.itemAnimator = null
        openOrdersRecyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        loadOrders()
        swipeLayout.setOnRefreshListener(this)
    }

    override fun onRefresh() {
        if (swipeLayout.isRefreshing) {
            loadOrders()
        }
    }

    private fun loadOrders() {
        chartProgressSpinner!!.visibility = View.VISIBLE
        if (KotlinUtils.apiDetailsPresent(requireContext())) {
            val timeStamp = KotlinUtils.generateTimeStamp()
            val method = "GET"
            val path = "/positions"
            val queryString = ""
            val payload = ""
            val signatureData = method + timeStamp + path + queryString + payload

            val signature = KotlinUtils.generateSignature(
                signatureData,
                appPreferenceManager!!.apiSecret!!
            )

            positionViewModel.getOpenPositions(
                appPreferenceManager!!.apiKey!!,
                timeStamp,
                signature!!
            )!!.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    openOrdersRecyclerView.adapter = PositionAdapter(it)
                }
                chartProgressSpinner!!.visibility = View.GONE
                if (swipeLayout.isRefreshing) {
                    swipeLayout.isRefreshing = false
                }
            })
        } else {
            chartProgressSpinner!!.visibility = View.GONE
            if (swipeLayout.isRefreshing) {
                swipeLayout.isRefreshing = false
            }
        }
    }
}