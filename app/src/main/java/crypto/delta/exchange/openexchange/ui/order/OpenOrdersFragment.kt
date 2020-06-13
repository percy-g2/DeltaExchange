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
import crypto.delta.exchange.openexchange.adapter.OpenOrdersAdapter
import crypto.delta.exchange.openexchange.ui.base.BaseFragment
import crypto.delta.exchange.openexchange.utils.KotlinUtils
import kotlinx.android.synthetic.main.fragment_open_orders.*

class OpenOrdersFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var openOrdersViewModel: OpenOrdersViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        openOrdersViewModel =
            ViewModelProvider(this@OpenOrdersFragment).get(OpenOrdersViewModel::class.java)
        openOrdersViewModel.init(requireActivity())
        return inflater.inflate(R.layout.fragment_open_orders, container, false)
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
        val timeStamp = KotlinUtils.generateTimeStamp()
        val method = "GET"
        val path = "/orders"
        val queryString = "?state=open"
        val payload = ""
        val signatureData = method + timeStamp + path + queryString + payload

        val signature = KotlinUtils.generateSignature(
            signatureData,
            appPreferenceManager!!.apiSecret!!
        )

        openOrdersViewModel.getOrders(appPreferenceManager!!.apiKey!!,
            timeStamp,
            signature!!,
            appPreferenceManager!!.currentProductId!!, "open")!!.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                openOrdersRecyclerView.adapter = OpenOrdersAdapter(it)
            }
            chartProgressSpinner!!.visibility = View.GONE
            if (swipeLayout.isRefreshing) {
                swipeLayout.isRefreshing = false
            }
        })
    }
}