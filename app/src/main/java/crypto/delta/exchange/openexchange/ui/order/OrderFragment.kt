package crypto.delta.exchange.openexchange.ui.order

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.warkiz.tickseekbar.OnSeekChangeListener
import com.warkiz.tickseekbar.SeekParams
import com.warkiz.tickseekbar.TickSeekBar
import crypto.delta.exchange.openexchange.R
import crypto.delta.exchange.openexchange.api.NoConnectivityException
import crypto.delta.exchange.openexchange.pojo.ErrorResponse
import crypto.delta.exchange.openexchange.pojo.order.ChangeOrderLeverageBody
import crypto.delta.exchange.openexchange.pojo.order.CreateOrderRequest
import crypto.delta.exchange.openexchange.pojo.order.OrderLeverageResponse
import crypto.delta.exchange.openexchange.ui.base.BaseFragment
import crypto.delta.exchange.openexchange.utils.KotlinUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_order.*
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException

class OrderFragment : BaseFragment() {
    private val logTag: String? = OrderFragment::class.java.simpleName
    private var sectionsPagerAdapter: SectionsPagerAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        return inflater.inflate(R.layout.fragment_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dialog = KotlinUtils.showProgressBar(
            requireContext(),
            requireContext().resources.getString(R.string.please_wait)
        )
        lifecycleScope.launch {
            delay(300)
            currentSymbol.text = appPreferenceManager!!.currentProductSymbol!!
            sectionsPagerAdapter = SectionsPagerAdapter(childFragmentManager)
            viewPager!!.adapter = sectionsPagerAdapter
            viewPager!!.currentItem = 0

            orderPositionTabLayout.addOnTabSelectedListener(object :
                TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    viewPager.currentItem = orderPositionTabLayout.selectedTabPosition
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    viewPager.currentItem = orderPositionTabLayout.selectedTabPosition
                }

                override fun onTabUnselected(p0: TabLayout.Tab?) {

                }
            })
            viewPager.addOnPageChangeListener(
                TabLayout.TabLayoutOnPageChangeListener(
                    orderPositionTabLayout
                )
            )

            val orderBookRecentTradeSectionsPagerAdapter =
                OrderBookRecentTradeSectionsPagerAdapter(childFragmentManager)
            orderBookRecentTradeViewPage!!.adapter = orderBookRecentTradeSectionsPagerAdapter
            orderBookRecentTradeViewPage!!.currentItem = 0

            orderBookRecentTradeTabLayout.addOnTabSelectedListener(object :
                TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    orderBookRecentTradeViewPage.currentItem =
                        orderBookRecentTradeTabLayout.selectedTabPosition
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    orderBookRecentTradeViewPage.currentItem =
                        orderBookRecentTradeTabLayout.selectedTabPosition
                }

                override fun onTabUnselected(p0: TabLayout.Tab?) {

                }
            })
            orderBookRecentTradeViewPage.addOnPageChangeListener(
                TabLayout.TabLayoutOnPageChangeListener(
                    orderBookRecentTradeTabLayout
                )
            )

            if (!KotlinUtils.apiDetailsPresent(requireContext())) {
                btnPlaceOrder.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.gray)
            }
            var leverageChangedByUser = false
            val progressBar = KotlinUtils.showProgressBar(
                requireActivity(),
                requireContext().resources.getString(R.string.please_wait)
            )

            if (KotlinUtils.apiDetailsPresent(requireContext())) {
                val timeStamp = KotlinUtils.generateTimeStamp()
                val method = "GET"
                val path = "/orders/leverage"
                val queryString = "?product_id=" + appPreferenceManager!!.currentProductId!!
                val payload = ""
                val signatureData = method + timeStamp + path + queryString + payload
                Log.d(logTag, signatureData)
                val signature = KotlinUtils.generateSignature(
                    signatureData,
                    appPreferenceManager!!.apiSecret!!
                )

                val observe = service!!.getOrderLeverage(
                    appPreferenceManager!!.apiKey!!,
                    timeStamp,
                    signature!!,
                    appPreferenceManager!!.currentProductId!!
                )
                observe.enqueue(object :
                    Callback<ResponseBody?> {
                    override fun onResponse(
                        call: Call<ResponseBody?>?,
                        response: Response<ResponseBody?>
                    ) {
                        if (response.code() == 200) {
                            val orderLeverageResponse = Gson().fromJson(
                                response.body()!!.charStream(),
                                OrderLeverageResponse::class.java
                            )
                            when {
                                orderLeverageResponse.leverage.toDouble() == 1.0 -> {
                                    leverageSeeker.setProgress(0f)
                                }
                                orderLeverageResponse.leverage.toDouble() == 2.0 -> {
                                    leverageSeeker.setProgress(14f)
                                }
                                orderLeverageResponse.leverage.toDouble() == 3.0 -> {
                                    leverageSeeker.setProgress(29f)
                                }
                                orderLeverageResponse.leverage.toDouble() == 5.0 -> {
                                    leverageSeeker.setProgress(43f)
                                }
                                orderLeverageResponse.leverage.toDouble() == 10.0 -> {
                                    leverageSeeker.setProgress(57f)
                                }
                                orderLeverageResponse.leverage.toDouble() == 25.0 -> {
                                    leverageSeeker.setProgress(71f)
                                }
                                orderLeverageResponse.leverage.toDouble() == 50.0 -> {
                                    leverageSeeker.setProgress(86f)
                                }
                                orderLeverageResponse.leverage.toDouble() == 100.0 -> {
                                    leverageSeeker.setProgress(100f)
                                }
                            }

                            leverageTxt.text =
                                orderLeverageResponse.leverage.toDouble().toInt().toString()
                                    .plus("x")
                            leverageChangedByUser = true
                            Toasty.success(
                                requireContext(),
                                requireContext().getString(R.string.order_leverage_updated),
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                            if (buyAndSellSwitch.isChecked) {
                                buyAndSellSwitch.text =
                                    requireContext().resources.getString(R.string.sell_short)
                                buyAndSellSwitch.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.colorAsk
                                    )
                                )
                                btnPlaceOrder.backgroundTintList =
                                    ContextCompat.getColorStateList(
                                        requireContext(),
                                        R.color.colorAsk
                                    )
                            } else {
                                buyAndSellSwitch.text =
                                    requireContext().resources.getString(R.string.buy_long)
                                buyAndSellSwitch.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.colorBid
                                    )
                                )
                                btnPlaceOrder.backgroundTintList =
                                    ContextCompat.getColorStateList(
                                        requireContext(),
                                        R.color.colorBid
                                    )
                            }
                        } else {
                            val errorBody = Gson().fromJson(
                                response.errorBody()!!.charStream(),
                                ErrorResponse::class.java
                            )
                            Toasty.error(
                                requireContext(),
                                errorBody.message,
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                            btnPlaceOrder.backgroundTintList =
                                ContextCompat.getColorStateList(requireContext(), R.color.gray)
                        }
                        progressBar.dismiss()
                    }

                    override fun onFailure(call: Call<ResponseBody?>?, error: Throwable?) {
                        when (error) {
                            is NoConnectivityException -> {
                                Toasty.info(
                                    requireContext(),
                                    requireContext().getString(R.string.active_network_connection_required),
                                    Toast.LENGTH_SHORT,
                                    true
                                ).show()
                            }
                            is ConnectException -> {
                                Toasty.error(
                                    requireContext(),
                                    requireContext().getString(R.string.server_not_responding),
                                    Toast.LENGTH_SHORT,
                                    true
                                ).show()
                            }
                            is SocketTimeoutException -> {
                                Toasty.error(
                                    requireContext(),
                                    requireContext().getString(R.string.connection_timeout),
                                    Toast.LENGTH_SHORT,
                                    true
                                ).show()
                            }
                            else -> {
                                Toasty.error(
                                    requireContext(),
                                    requireContext().getString(R.string.something_wrong),
                                    Toast.LENGTH_SHORT,
                                    true
                                ).show()
                                error!!.printStackTrace()
                            }
                        }
                        btnPlaceOrder.backgroundTintList =
                            ContextCompat.getColorStateList(requireContext(), R.color.gray)
                        progressBar.dismiss()
                        leverageChangedByUser = true
                    }
                })
            } else {
                progressBar.dismiss()
                Toasty.warning(
                    requireContext(),
                    requireContext().getString(R.string.api_details_error),
                    Toast.LENGTH_SHORT,
                    true
                ).show()
            }

            orderTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    when (orderTypeSpinner.selectedItem.toString()) {
                        "Limit" -> {
                            edtLimitPrice.visibility = View.VISIBLE
                            edtLimitPrice.hint = resources.getString(R.string.limit_price)
                            tifLayout.visibility = View.VISIBLE
                            checkPostOnly.visibility = View.VISIBLE
                            edtStopPrice.visibility = View.GONE
                        }
                        "Market" -> {
                            edtLimitPrice.visibility = View.GONE
                            tifLayout.visibility = View.GONE
                            checkPostOnly.visibility = View.GONE
                            edtStopPrice.visibility = View.GONE
                        }
                        "Stop Market" -> {
                            edtLimitPrice.visibility = View.GONE
                            edtStopPrice.visibility = View.VISIBLE
                            tifLayout.visibility = View.GONE
                            checkPostOnly.visibility = View.GONE
                        }
                        "Stop Limit" -> {
                            edtLimitPrice.visibility = View.VISIBLE
                            edtStopPrice.visibility = View.VISIBLE
                            tifLayout.visibility = View.GONE
                            checkPostOnly.visibility = View.GONE
                        }
                        "Trailing Stop" -> {
                            edtLimitPrice.visibility = View.VISIBLE
                            edtLimitPrice.hint = resources.getString(R.string.trailing_amount)
                            tifLayout.visibility = View.GONE
                            checkPostOnly.visibility = View.GONE
                            edtStopPrice.visibility = View.GONE
                        }
                    }
                }

            }

            when (orderTypeSpinner.selectedItem.toString()) {
                "Limit" -> {
                    edtLimitPrice.visibility = View.VISIBLE
                    edtLimitPrice.hint = resources.getString(R.string.limit_price)
                    tifLayout.visibility = View.VISIBLE
                    checkPostOnly.visibility = View.VISIBLE
                    edtStopPrice.visibility = View.GONE
                }
                "Market" -> {
                    edtLimitPrice.visibility = View.GONE
                    tifLayout.visibility = View.GONE
                    checkPostOnly.visibility = View.GONE
                    edtStopPrice.visibility = View.GONE
                }
                "Stop Market" -> {
                    edtLimitPrice.visibility = View.GONE
                    edtStopPrice.visibility = View.VISIBLE
                    tifLayout.visibility = View.GONE
                    checkPostOnly.visibility = View.GONE
                }
                "Stop Limit" -> {
                    edtLimitPrice.visibility = View.VISIBLE
                    edtStopPrice.visibility = View.VISIBLE
                    tifLayout.visibility = View.GONE
                    checkPostOnly.visibility = View.GONE
                }
                "Trailing Stop" -> {
                    edtLimitPrice.visibility = View.VISIBLE
                    edtLimitPrice.hint = resources.getString(R.string.trailing_amount)
                    tifLayout.visibility = View.GONE
                    checkPostOnly.visibility = View.GONE
                    edtStopPrice.visibility = View.GONE
                }
            }

            if (KotlinUtils.apiDetailsPresent(requireContext())) {
                buyAndSellSwitch.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        buyAndSellSwitch.text =
                            requireContext().resources.getString(R.string.sell_short)
                        buyAndSellSwitch.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorAsk
                            )
                        )
                        btnPlaceOrder.backgroundTintList =
                            ContextCompat.getColorStateList(requireContext(), R.color.colorAsk)
                    } else {
                        buyAndSellSwitch.text =
                            requireContext().resources.getString(R.string.buy_long)
                        buyAndSellSwitch.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorBid
                            )
                        )
                        btnPlaceOrder.backgroundTintList =
                            ContextCompat.getColorStateList(requireContext(), R.color.colorBid)
                    }
                }
            }

            leverageSeeker.onSeekChangeListener = object : OnSeekChangeListener {
                override fun onSeeking(seekParams: SeekParams) {
                    Log.i(logTag, seekParams.seekBar.toString())
                    Log.i(logTag, seekParams.progress.toString())
                    Log.i(logTag, seekParams.progressFloat.toString())
                    Log.i(logTag, seekParams.fromUser.toString())
                    //when tick count > 0
                    Log.i(logTag, seekParams.thumbPosition.toString())
                    Log.i(logTag, seekParams.tickText)
                    leverageTxt.text = seekParams.tickText

                    if (leverageChangedByUser) {
                        val changeOrderLeverageBody = ChangeOrderLeverageBody()
                        changeOrderLeverageBody.productId =
                            appPreferenceManager!!.currentProductId!!.toInt()
                        when (seekParams.progress) {
                            0 -> {
                                changeOrderLeverageBody.leverage = "1.0"
                            }
                            14 -> {
                                changeOrderLeverageBody.leverage = "2.0"
                            }
                            29 -> {
                                changeOrderLeverageBody.leverage = "3.0"
                            }
                            43 -> {
                                changeOrderLeverageBody.leverage = "5.0"
                            }
                            57 -> {
                                changeOrderLeverageBody.leverage = "10.0"
                            }
                            71 -> {
                                changeOrderLeverageBody.leverage = "25.0"
                            }
                            86 -> {
                                changeOrderLeverageBody.leverage = "50.0"
                            }
                            100 -> {
                                changeOrderLeverageBody.leverage = "100.0"
                            }
                        }

                        val timeStampSetOrderLeverage = KotlinUtils.generateTimeStamp()
                        val methodSetOrderLeverage = "POST"
                        val pathSetOrderLeverage = "/orders/leverage"
                        val queryStringSetOrderLeverage = ""
                        val gson = Gson()
                        val payloadSetOrderLeverage =
                            gson.toJson(changeOrderLeverageBody).toString()
                        val signatureDataSetOrderLeverage =
                            methodSetOrderLeverage + timeStampSetOrderLeverage + pathSetOrderLeverage + queryStringSetOrderLeverage + payloadSetOrderLeverage
                        val signatureSetOrderLeverage = KotlinUtils.generateSignature(
                            signatureDataSetOrderLeverage,
                            appPreferenceManager!!.apiSecret!!
                        )

                        progressBar.show()
                        val observeOrderLeverage = service!!.setOrderLeverage(
                            appPreferenceManager!!.apiKey!!,
                            timeStampSetOrderLeverage,
                            signatureSetOrderLeverage!!,
                            changeOrderLeverageBody
                        )

                        observeOrderLeverage.enqueue(object :
                            Callback<ResponseBody?> {
                            override fun onResponse(
                                call: Call<ResponseBody?>?,
                                response: Response<ResponseBody?>
                            ) {
                                if (response.code() == 200) {
                                    Toasty.success(
                                        requireContext(),
                                        requireContext().getString(R.string.order_leverage_changed_successfully),
                                        Toast.LENGTH_SHORT,
                                        true
                                    ).show()
                                } else {
                                    val errorBody = Gson().fromJson(
                                        response.errorBody()!!.charStream(),
                                        ErrorResponse::class.java
                                    )
                                    Toasty.error(
                                        requireContext(),
                                        errorBody.message,
                                        Toast.LENGTH_SHORT,
                                        true
                                    ).show()
                                }
                                progressBar.dismiss()
                            }

                            override fun onFailure(call: Call<ResponseBody?>?, error: Throwable?) {
                                when (error) {
                                    is NoConnectivityException -> {
                                        Toasty.info(
                                            requireContext(),
                                            requireContext().getString(R.string.active_network_connection_required),
                                            Toast.LENGTH_SHORT,
                                            true
                                        ).show()
                                    }
                                    is ConnectException -> {
                                        Toasty.error(
                                            requireContext(),
                                            requireContext().getString(R.string.server_not_responding),
                                            Toast.LENGTH_SHORT,
                                            true
                                        ).show()
                                    }
                                    is SocketTimeoutException -> {
                                        Toasty.error(
                                            requireContext(),
                                            requireContext().getString(R.string.connection_timeout),
                                            Toast.LENGTH_SHORT,
                                            true
                                        ).show()
                                    }
                                    else -> {
                                        Toasty.error(
                                            requireContext(),
                                            requireContext().getString(R.string.something_wrong),
                                            Toast.LENGTH_SHORT,
                                            true
                                        ).show()
                                        error!!.printStackTrace()
                                    }
                                }
                                progressBar.dismiss()
                            }
                        })
                    } else {
                        Toasty.error(
                            requireContext(),
                            requireContext().getString(R.string.api_details_error),
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                    }
                }

                override fun onStartTrackingTouch(seekBar: TickSeekBar?) {}
                override fun onStopTrackingTouch(seekBar: TickSeekBar?) {}
            }

            checkPostOnly.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked && !checkedGTC.isChecked) {
                    checkPostOnly.isChecked = false
                    Toasty.error(
                        requireContext(),
                        requireContext().getString(R.string.post_only_flag_is_for_gtc),
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                }
            }

            checkedFOK.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked && checkPostOnly.isChecked) {
                    checkPostOnly.isChecked = false
                    Toasty.error(
                        requireContext(),
                        requireContext().getString(R.string.post_only_flag_is_for_gtc),
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                }
            }

            checkedIOC.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked && checkPostOnly.isChecked) {
                    checkPostOnly.isChecked = false
                    Toasty.error(
                        requireContext(),
                        requireContext().getString(R.string.post_only_flag_is_for_gtc),
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                }
            }

            btnPlaceOrder.setOnClickListener {
                if (orderTypeSpinner.selectedItem.toString().equals("Limit", true)) {
                    if (edtLimitPrice.text!!.isNotEmpty() && edtQuantity.text!!.isNotEmpty()) {
                        if (edtLimitPrice.text!!.toString()
                                .toDouble() != 0.0 && edtQuantity.text!!.toString()
                                .toDouble() != 0.0
                        ) {
                            val createOrderRequest = CreateOrderRequest()
                            createOrderRequest.orderType =
                                CreateOrderRequest.OrderType.limit_order
                            if (buyAndSellSwitch.isChecked) {
                                createOrderRequest.side = CreateOrderRequest.Side.sell
                            } else {
                                createOrderRequest.side = CreateOrderRequest.Side.buy
                            }
                            createOrderRequest.productId =
                                appPreferenceManager!!.currentProductId!!.toInt()
                            createOrderRequest.limitPrice = edtLimitPrice.text.toString()
                            createOrderRequest.size = edtQuantity.text.toString().toInt()
                            when {
                                checkedGTC.isChecked -> {
                                    createOrderRequest.timeInForce =
                                        CreateOrderRequest.TimeInForce.gtc
                                }
                                checkedFOK.isChecked -> {
                                    createOrderRequest.timeInForce =
                                        CreateOrderRequest.TimeInForce.fok
                                }
                                checkedIOC.isChecked -> {
                                    createOrderRequest.timeInForce =
                                        CreateOrderRequest.TimeInForce.ioc
                                }
                            }
                            if (checkPostOnly.isChecked) {
                                createOrderRequest.postOnly = "true"
                            } else {
                                createOrderRequest.postOnly = "false"
                            }
                            if (checkReduceOnly.isChecked) {
                                createOrderRequest.reduceOnly = "true"
                            } else {
                                createOrderRequest.reduceOnly = "false"
                            }
                            CoroutineScope(Dispatchers.Main).launch {
                                placeOrder(createOrderRequest)
                            }
                        } else {
                            if (edtLimitPrice.text.toString().toDouble() == 0.0) {
                                Toasty.error(
                                    requireContext(),
                                    requireContext().getString(R.string.limit_price_is_zero),
                                    Toast.LENGTH_SHORT,
                                    true
                                ).show()
                            }
                            if (edtQuantity.text.toString().toDouble() == 0.0) {
                                Toasty.error(
                                    requireContext(),
                                    requireContext().getString(R.string.quantity_is_zero),
                                    Toast.LENGTH_SHORT,
                                    true
                                ).show()
                            }
                        }
                    } else {
                        if (edtLimitPrice.text.isNullOrEmpty()) {
                            Toasty.error(
                                requireContext(),
                                requireContext().getString(R.string.limit_price_is_empty),
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                        }
                        if (edtQuantity.text.isNullOrEmpty()) {
                            Toasty.error(
                                requireContext(),
                                requireContext().getString(R.string.quantity_is_empty),
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                        }
                    }
                } else if (orderTypeSpinner.selectedItem.toString().equals("Market", true)) {
                    if (edtQuantity.text!!.isNotEmpty()) {
                        if (edtQuantity.text!!.toString().toDouble() != 0.0) {
                            val createOrderRequest = CreateOrderRequest()
                            createOrderRequest.orderType =
                                CreateOrderRequest.OrderType.market_order
                            if (buyAndSellSwitch.isChecked) {
                                createOrderRequest.side = CreateOrderRequest.Side.sell
                            } else {
                                createOrderRequest.side = CreateOrderRequest.Side.buy
                            }
                            createOrderRequest.productId =
                                appPreferenceManager!!.currentProductId!!.toInt()
                            createOrderRequest.size = edtQuantity.text.toString().toInt()
                            if (checkReduceOnly.isChecked) {
                                createOrderRequest.reduceOnly = "true"
                            } else {
                                createOrderRequest.reduceOnly = "false"
                            }
                            CoroutineScope(Dispatchers.Main).launch {
                                placeOrder(createOrderRequest)
                            }
                        } else {
                            if (edtQuantity.text.toString().toDouble() == 0.0) {
                                Toasty.error(
                                    requireContext(),
                                    requireContext().getString(R.string.quantity_is_zero),
                                    Toast.LENGTH_SHORT,
                                    true
                                ).show()
                            }
                        }
                    } else {
                        if (edtQuantity.text.isNullOrEmpty()) {
                            Toasty.error(
                                requireContext(),
                                requireContext().getString(R.string.quantity_is_empty),
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                        }
                    }
                } else if (orderTypeSpinner.selectedItem.toString().equals("Stop Limit", true)) {
                    if (edtLimitPrice.text!!.isNotEmpty() && edtQuantity.text!!.isNotEmpty()) {
                        if (edtLimitPrice.text!!.toString()
                                .toDouble() != 0.0 && edtQuantity.text!!.toString()
                                .toDouble() != 0.0
                        ) {
                            val createOrderRequest = CreateOrderRequest()
                            createOrderRequest.orderType =
                                CreateOrderRequest.OrderType.limit_order
                            if (buyAndSellSwitch.isChecked) {
                                createOrderRequest.side = CreateOrderRequest.Side.sell
                            } else {
                                createOrderRequest.side = CreateOrderRequest.Side.buy
                            }
                            createOrderRequest.productId =
                                appPreferenceManager!!.currentProductId!!.toInt()
                            createOrderRequest.limitPrice = edtLimitPrice.text.toString()
                            createOrderRequest.stopPrice = edtStopPrice.text.toString()
                            createOrderRequest.size = edtQuantity.text.toString().toInt()
                            createOrderRequest.stopOrderType = "stop_loss_order"
                            if (checkReduceOnly.isChecked) {
                                createOrderRequest.reduceOnly = "true"
                            } else {
                                createOrderRequest.reduceOnly = "false"
                            }
                            CoroutineScope(Dispatchers.Main).launch {
                                placeOrder(createOrderRequest)
                            }
                        } else {
                            if (edtLimitPrice.text.toString().toDouble() == 0.0) {
                                Toasty.error(
                                    requireContext(),
                                    requireContext().getString(R.string.limit_price_is_zero),
                                    Toast.LENGTH_SHORT,
                                    true
                                ).show()
                            }
                            if (edtStopPrice.text.toString().toDouble() == 0.0) {
                                Toasty.error(
                                    requireContext(),
                                    requireContext().getString(R.string.stop_price_is_zero),
                                    Toast.LENGTH_SHORT,
                                    true
                                ).show()
                            }
                            if (edtQuantity.text.toString().toDouble() == 0.0) {
                                Toasty.error(
                                    requireContext(),
                                    requireContext().getString(R.string.quantity_is_zero),
                                    Toast.LENGTH_SHORT,
                                    true
                                ).show()
                            }
                        }
                    } else {
                        if (edtLimitPrice.text.isNullOrEmpty()) {
                            Toasty.error(
                                requireContext(),
                                requireContext().getString(R.string.limit_price_is_empty),
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                        }
                        if (edtStopPrice.text.isNullOrEmpty()) {
                            Toasty.error(
                                requireContext(),
                                requireContext().getString(R.string.stop_price_is_empty),
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                        }
                        if (edtQuantity.text.isNullOrEmpty()) {
                            Toasty.error(
                                requireContext(),
                                requireContext().getString(R.string.quantity_is_empty),
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                        }
                    }
                } else if (orderTypeSpinner.selectedItem.toString().equals("Stop Market", true)) {
                    if (edtStopPrice.text!!.isNotEmpty() && edtQuantity.text!!.isNotEmpty()) {
                        if (edtLimitPrice.text!!.toString()
                                .toDouble() != 0.0 && edtQuantity.text!!.toString()
                                .toDouble() != 0.0
                        ) {
                            val createOrderRequest = CreateOrderRequest()
                            createOrderRequest.orderType =
                                CreateOrderRequest.OrderType.market_order
                            if (buyAndSellSwitch.isChecked) {
                                createOrderRequest.side = CreateOrderRequest.Side.sell
                            } else {
                                createOrderRequest.side = CreateOrderRequest.Side.buy
                            }
                            createOrderRequest.productId =
                                appPreferenceManager!!.currentProductId!!.toInt()
                            createOrderRequest.stopPrice = edtStopPrice.text.toString()
                            createOrderRequest.size = edtQuantity.text.toString().toInt()
                            createOrderRequest.closeOnTrigger = "false"
                            createOrderRequest.stopOrderType = "stop_loss_order"
                            if (checkReduceOnly.isChecked) {
                                createOrderRequest.reduceOnly = "true"
                            } else {
                                createOrderRequest.reduceOnly = "false"
                            }
                            CoroutineScope(Dispatchers.Main).launch {
                                placeOrder(createOrderRequest)
                            }
                        } else {
                            if (edtStopPrice.text.toString().toDouble() == 0.0) {
                                Toasty.error(
                                    requireContext(),
                                    requireContext().getString(R.string.stop_price_is_zero),
                                    Toast.LENGTH_SHORT,
                                    true
                                ).show()
                            }
                            if (edtQuantity.text.toString().toDouble() == 0.0) {
                                Toasty.error(
                                    requireContext(),
                                    requireContext().getString(R.string.quantity_is_zero),
                                    Toast.LENGTH_SHORT,
                                    true
                                ).show()
                            }
                        }
                    } else {
                        if (edtStopPrice.text.isNullOrEmpty()) {
                            Toasty.error(
                                requireContext(),
                                requireContext().getString(R.string.stop_price_is_empty),
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                        }
                        if (edtQuantity.text.isNullOrEmpty()) {
                            Toasty.error(
                                requireContext(),
                                requireContext().getString(R.string.quantity_is_empty),
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                        }
                    }
                } else if (orderTypeSpinner.selectedItem.toString().equals("Trailing Stop", true)) {
                    if (edtLimitPrice.text!!.isNotEmpty() && edtQuantity.text!!.isNotEmpty()) {
                        if (edtLimitPrice.text!!.toString()
                                .toDouble() != 0.0 && edtQuantity.text!!.toString()
                                .toDouble() != 0.0
                        ) {
                            val createOrderRequest = CreateOrderRequest()
                            createOrderRequest.orderType =
                                CreateOrderRequest.OrderType.market_order
                            if (buyAndSellSwitch.isChecked) {
                                createOrderRequest.side = CreateOrderRequest.Side.sell
                            } else {
                                createOrderRequest.side = CreateOrderRequest.Side.buy
                            }
                            createOrderRequest.productId =
                                appPreferenceManager!!.currentProductId!!.toInt()
                            createOrderRequest.trailAmount = edtLimitPrice.text.toString()
                            createOrderRequest.size = edtQuantity.text.toString().toInt()
                            createOrderRequest.closeOnTrigger = "false"
                            createOrderRequest.stopOrderType = "stop_loss_order"
                            if (checkReduceOnly.isChecked) {
                                createOrderRequest.reduceOnly = "true"
                            } else {
                                createOrderRequest.reduceOnly = "false"
                            }
                            CoroutineScope(Dispatchers.Main).launch {
                                placeOrder(createOrderRequest)
                            }
                        } else {
                            if (edtLimitPrice.text.toString().toDouble() == 0.0) {
                                Toasty.error(
                                    requireContext(),
                                    requireContext().getString(R.string.trailing_amount_is_zero),
                                    Toast.LENGTH_SHORT,
                                    true
                                ).show()
                            }
                            if (edtQuantity.text.toString().toDouble() == 0.0) {
                                Toasty.error(
                                    requireContext(),
                                    requireContext().getString(R.string.quantity_is_zero),
                                    Toast.LENGTH_SHORT,
                                    true
                                ).show()
                            }
                        }
                    } else {
                        if (edtLimitPrice.text.isNullOrEmpty()) {
                            Toasty.error(
                                requireContext(),
                                requireContext().getString(R.string.trailing_amount_is_empty),
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                        }
                        if (edtQuantity.text.isNullOrEmpty()) {
                            Toasty.error(
                                requireContext(),
                                requireContext().getString(R.string.quantity_is_empty),
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                        }
                    }
                }
            }
            dialog.dismiss()
        }
    }

    private suspend fun placeOrder(createOrderRequest: CreateOrderRequest) {
        val dialog = KotlinUtils.showProgressBar(
            requireActivity(),
            requireContext().resources.getString(R.string.please_wait)
        )
        withContext(Dispatchers.Default) {
            val timeStamp = KotlinUtils.generateTimeStamp()
            val method = "POST"
            val path = "/orders"
            val gson = Gson()
            val payload = gson.toJson(createOrderRequest).toString()
            val signatureData = method + timeStamp + path + payload
            Log.d(logTag, signatureData)
            val signature = KotlinUtils.generateSignature(
                signatureData,
                appPreferenceManager!!.apiSecret!!
            )

            val observe = service!!.createOrder(
                appPreferenceManager!!.apiKey!!,
                timeStamp,
                signature!!,
                createOrderRequest
            )
            observe.enqueue(object :
                Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>?,
                    response: Response<ResponseBody?>
                ) {
                    if (response.code() == 200) {
                        Toasty.success(
                            requireContext(),
                            requireContext().getString(R.string.order_placed_successfully),
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                        sectionsPagerAdapter!!.notifyDataSetChanged()
                        edtLimitPrice.editableText.clear()
                        edtStopPrice.editableText.clear()
                        edtQuantity.editableText.clear()
                    } else {
                        val errorBody = Gson().fromJson(
                            response.errorBody()!!.charStream(),
                            ErrorResponse::class.java
                        )
                        Toasty.error(
                            requireContext(),
                            errorBody.message,
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>?, error: Throwable?) {
                    when (error) {
                        is NoConnectivityException -> {
                            Toasty.info(
                                requireContext(),
                                requireContext().getString(R.string.active_network_connection_required),
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                        }
                        is ConnectException -> {
                            Toasty.error(
                                requireContext(),
                                requireContext().getString(R.string.server_not_responding),
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                        }
                        is SocketTimeoutException -> {
                            Toasty.error(
                                requireContext(),
                                requireContext().getString(R.string.connection_timeout),
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                        }
                        else -> {
                            Toasty.error(
                                requireContext(),
                                requireContext().getString(R.string.something_wrong),
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                            error!!.printStackTrace()
                        }
                    }
                }
            })
        }
        withContext(Dispatchers.Main) {
            dialog.dismiss()
        }
    }

    /**
     * Used for tab paging...
     */
    inner class SectionsPagerAdapter internal constructor(fm: FragmentManager?) :
        FragmentPagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> {
                    // find first fragment...
                    PositionsFragment()
                }
                1 -> {
                    // find second fragment...
                    OpenOrdersFragment()
                }
                else -> PositionsFragment()
            }
        }

        override fun getItemPosition(`object`: Any): Int {
            if (`object` is OpenOrdersFragment) {
                `object`.update()
            }
            if (`object` is PositionsFragment) {
                `object`.update()
            }
            return super.getItemPosition(`object`)
        }

        override fun getCount(): Int {
            // Show 2 total pages.
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 -> return resources.getString(R.string.position)
                1 -> return resources.getString(R.string.open_orders)
            }
            return "null"
        }
    }

    /**
     * Used for tab paging...
     */
    inner class OrderBookRecentTradeSectionsPagerAdapter internal constructor(fm: FragmentManager?) :
        FragmentPagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> {
                    // find first fragment...
                    OrderBookFragment()
                }
                1 -> {
                    // find second fragment...
                    RecentTradesFragment()
                }
                else -> OrderBookFragment()
            }
        }

        override fun getCount(): Int {
            // Show 2 total pages.
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 -> return resources.getString(R.string.order_book)
                1 -> return resources.getString(R.string.recent_trade)
            }
            return "null"
        }
    }
}