package crypto.delta.exchange.openexchange.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import crypto.delta.exchange.openexchange.R
import crypto.delta.exchange.openexchange.adapter.StopOrdersAdapter
import crypto.delta.exchange.openexchange.interfaces.OrdersInterface
import crypto.delta.exchange.openexchange.pojo.DeleteOrderRequest
import crypto.delta.exchange.openexchange.pojo.ErrorResponse
import crypto.delta.exchange.openexchange.pojo.order.CreateOrderResponse
import crypto.delta.exchange.openexchange.ui.base.BaseFragment
import crypto.delta.exchange.openexchange.utils.KotlinUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_stop_orders.*

class StopOrdersFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener, OrdersInterface {
    private lateinit var openOrdersViewModel: OpenOrdersViewModel
    private var ordersInterface: OrdersInterface? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        openOrdersViewModel =
            ViewModelProvider(this@StopOrdersFragment).get(OpenOrdersViewModel::class.java)
        openOrdersViewModel.init()
        return inflater.inflate(R.layout.fragment_stop_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ordersInterface = this@StopOrdersFragment
        stopOrdersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        stopOrdersRecyclerView.itemAnimator = null
        stopOrdersRecyclerView.addItemDecoration(
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
        progressSpinner!!.visibility = View.VISIBLE
        if (KotlinUtils.apiDetailsPresent(requireContext())) {
            val timeStamp = KotlinUtils.generateTimeStamp()
            val method = "GET"
            val path = "/orders"
            val queryString = "?state=pending&stop_order_type=stop_loss_order"
            val payload = ""
            val signatureData = method + timeStamp + path + queryString + payload

            val signature = KotlinUtils.generateSignature(
                signatureData,
                appPreferenceManager!!.apiSecret!!
            )

            openOrdersViewModel.getStopOrders(
                appPreferenceManager!!.apiKey!!,
                timeStamp,
                signature!!,
                "stop_loss_order", "pending"
            )!!.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    stopOrdersRecyclerView.adapter = StopOrdersAdapter(it, ordersInterface!!)
                }
                progressSpinner!!.visibility = View.GONE
                if (swipeLayout.isRefreshing) {
                    swipeLayout.isRefreshing = false
                }
            })
        } else {
            progressSpinner!!.visibility = View.GONE
            if (swipeLayout.isRefreshing) {
                swipeLayout.isRefreshing = false
            }
        }
    }

    fun update() {
        loadOrders()
    }

    override fun onCancelOrder(createOrderResponse: CreateOrderResponse) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirm")
            .setMessage("Do you want to close this order?")
            .setPositiveButton("Ok") { dialogInterface, _ ->
                cancelOrder(createOrderResponse)
                dialogInterface.dismiss()
            }
            .setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun cancelOrder(createOrderResponse: CreateOrderResponse) {
        val dialog = KotlinUtils.showProgressBar(
            requireContext(),
            requireContext().resources.getString(R.string.please_wait)
        )
        val deleteOrderRequest = DeleteOrderRequest()
        deleteOrderRequest.id = createOrderResponse.id
        deleteOrderRequest.productId = createOrderResponse.product!!.id
        val timeStamp = KotlinUtils.generateTimeStamp()
        val method = "DELETE"
        val path = "/orders"
        val queryString = ""
        val gson = Gson()
        val payload = gson.toJson(deleteOrderRequest).toString()
        val signatureData = method + timeStamp + path + queryString + payload

        val signature = KotlinUtils.generateSignature(
            signatureData,
            appPreferenceManager!!.apiSecret!!
        )

        openOrdersViewModel.cancelOrder(
            appPreferenceManager!!.apiKey!!,
            timeStamp,
            signature!!,
            deleteOrderRequest
        )!!.observe(viewLifecycleOwner, Observer {
            if (it.code() == 200) {
                Toasty.success(
                    requireContext(),
                    requireContext().getString(R.string.order_cancelled_successfully),
                    Toast.LENGTH_SHORT,
                    true
                ).show()
                loadOrders()
            } else {
                val errorBody = Gson().fromJson(
                    it.errorBody()!!.charStream(),
                    ErrorResponse::class.java
                )
                Toasty.error(
                    requireContext(),
                    errorBody.message!!,
                    Toast.LENGTH_SHORT,
                    true
                ).show()
            }
            dialog.dismiss()
        })
    }
}