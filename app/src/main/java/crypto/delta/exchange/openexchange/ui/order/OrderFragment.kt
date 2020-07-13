package crypto.delta.exchange.openexchange.ui.order

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import crypto.delta.exchange.openexchange.databinding.CreateOrderBinding
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


                            currentLeverage.text =
                                resources.getString(R.string.current_leverage).plus(
                                    orderLeverageResponse.leverage.toDouble().toInt().toString()
                                        .plus("x")
                                )
                            Toasty.success(
                                requireContext(),
                                requireContext().getString(R.string.order_leverage_updated),
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                            if (buyAndSellSwitch.isChecked) {
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
                            } else {
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
                            }
                        } else {
                            val errorBody = Gson().fromJson(
                                response.errorBody()!!.charStream(),
                                ErrorResponse::class.java
                            )
                            Toasty.error(
                                requireContext(),
                                errorBody.message!!,
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

            if (KotlinUtils.apiDetailsPresent(requireContext())) {
                buyAndSellSwitch.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
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
                    } else {
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
                    }
                }
            }

            btnPlaceOrder.setOnClickListener {
                if (edtPrice.text!!.isNotEmpty() && edtQuantity.text!!.isNotEmpty()) {
                    if (edtPrice.text!!.toString()
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
                        createOrderRequest.limitPrice = edtPrice.text.toString()
                        createOrderRequest.size = edtQuantity.text.toString().toInt()
                        createOrderRequest.timeInForce =
                            CreateOrderRequest.TimeInForce.gtc
                        createOrderRequest.postOnly = "false"
                        createOrderRequest.reduceOnly = "false"
                        CoroutineScope(Dispatchers.Main).launch {
                            placeOrder(createOrderRequest, false, null)
                        }
                    } else {
                        if (edtPrice.text.toString().toDouble() == 0.0) {
                            Toasty.error(
                                requireContext(),
                                requireContext().getString(R.string.price_is_zero),
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
                    if (edtPrice.text.isNullOrEmpty()) {
                        Toasty.error(
                            requireContext(),
                            requireContext().getString(R.string.price_is_empty),
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
            showAdvancedOrderForm.setOnClickListener {
                openAdvanceOrderDialog()
            }
            dialog.dismiss()
        }
    }

    private fun openAdvanceOrderDialog() {

        val dialog = AlertDialog.Builder(requireContext(), R.style.createOrderDialog).create()

        val createOrderBinding = CreateOrderBinding.inflate(dialog.layoutInflater)
        dialog.setView(createOrderBinding.root)

        val window: Window = dialog.window!!
        window.setLayout(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.show()

        if (KotlinUtils.apiDetailsPresent(requireContext())) {
            val currentLeverageValue =
                currentLeverage.text.toString().replace("[^0-9]".toRegex(), "")
            createOrderBinding.leverageTxt.text =
                currentLeverage.text.toString().replace("[^0-9]".toRegex(), "")
                    .plus("x")
            when {
                currentLeverageValue.toDouble() == 1.0 -> {
                    createOrderBinding.leverageSeeker.setProgress(0f)
                }
                currentLeverageValue.toDouble() == 2.0 -> {
                    createOrderBinding.leverageSeeker.setProgress(14f)
                }
                currentLeverageValue.toDouble() == 3.0 -> {
                    createOrderBinding.leverageSeeker.setProgress(29f)
                }
                currentLeverageValue.toDouble() == 5.0 -> {
                    createOrderBinding.leverageSeeker.setProgress(43f)
                }
                currentLeverageValue.toDouble() == 10.0 -> {
                    createOrderBinding.leverageSeeker.setProgress(57f)
                }
                currentLeverageValue.toDouble() == 25.0 -> {
                    createOrderBinding.leverageSeeker.setProgress(71f)
                }
                currentLeverageValue.toDouble() == 50.0 -> {
                    createOrderBinding.leverageSeeker.setProgress(86f)
                }
                currentLeverageValue.toDouble() == 100.0 -> {
                    createOrderBinding.leverageSeeker.setProgress(100f)
                }
            }
        } else {
            Toasty.warning(
                requireContext(),
                requireContext().getString(R.string.api_details_error),
                Toast.LENGTH_SHORT,
                true
            ).show()
        }

        createOrderBinding.orderTypeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    when (createOrderBinding.orderTypeSpinner.selectedItem.toString()) {
                        "Limit" -> {
                            createOrderBinding.edtLimitPrice.visibility = View.VISIBLE
                            createOrderBinding.edtLimitPriceTextInputLayout.hint =
                                resources.getString(R.string.limit_price)
                            createOrderBinding.tifLayout.visibility = View.VISIBLE
                            createOrderBinding.checkPostOnly.visibility = View.VISIBLE
                            createOrderBinding.edtStopPriceTextInputLayout.visibility = View.GONE
                        }
                        "Market" -> {
                            createOrderBinding.edtLimitPrice.visibility = View.GONE
                            createOrderBinding.tifLayout.visibility = View.GONE
                            createOrderBinding.checkPostOnly.visibility = View.GONE
                            createOrderBinding.edtStopPriceTextInputLayout.visibility = View.GONE
                        }
                        "Stop Market" -> {
                            createOrderBinding.edtLimitPrice.visibility = View.GONE
                            createOrderBinding.edtStopPriceTextInputLayout.visibility = View.VISIBLE
                            createOrderBinding.tifLayout.visibility = View.GONE
                            createOrderBinding.checkPostOnly.visibility = View.GONE
                        }
                        "Stop Limit" -> {
                            createOrderBinding.edtLimitPrice.visibility = View.VISIBLE
                            createOrderBinding.edtStopPriceTextInputLayout.visibility = View.VISIBLE
                            createOrderBinding.tifLayout.visibility = View.GONE
                            createOrderBinding.checkPostOnly.visibility = View.GONE
                        }
                        "Trailing Stop" -> {
                            createOrderBinding.edtLimitPrice.visibility = View.VISIBLE
                            createOrderBinding.edtLimitPriceTextInputLayout.hint =
                                resources.getString(R.string.trailing_amount)
                            createOrderBinding.tifLayout.visibility = View.GONE
                            createOrderBinding.checkPostOnly.visibility = View.GONE
                            createOrderBinding.edtStopPriceTextInputLayout.visibility = View.GONE
                        }
                    }
                }

            }

        when (createOrderBinding.orderTypeSpinner.selectedItem.toString()) {
            "Limit" -> {
                createOrderBinding.edtLimitPrice.visibility = View.VISIBLE
                createOrderBinding.edtLimitPriceTextInputLayout.hint =
                    resources.getString(R.string.limit_price)
                createOrderBinding.tifLayout.visibility = View.VISIBLE
                createOrderBinding.checkPostOnly.visibility = View.VISIBLE
                createOrderBinding.edtStopPriceTextInputLayout.visibility = View.GONE
            }
            "Market" -> {
                createOrderBinding.edtLimitPrice.visibility = View.GONE
                createOrderBinding.tifLayout.visibility = View.GONE
                createOrderBinding.checkPostOnly.visibility = View.GONE
                createOrderBinding.edtStopPriceTextInputLayout.visibility = View.GONE
            }
            "Stop Market" -> {
                createOrderBinding.edtLimitPrice.visibility = View.GONE
                createOrderBinding.edtStopPriceTextInputLayout.visibility = View.VISIBLE
                createOrderBinding.tifLayout.visibility = View.GONE
                createOrderBinding.checkPostOnly.visibility = View.GONE
            }
            "Stop Limit" -> {
                createOrderBinding.edtLimitPrice.visibility = View.VISIBLE
                createOrderBinding.edtStopPriceTextInputLayout.visibility = View.VISIBLE
                createOrderBinding.tifLayout.visibility = View.GONE
                createOrderBinding.checkPostOnly.visibility = View.GONE
            }
            "Trailing Stop" -> {
                createOrderBinding.edtLimitPrice.visibility = View.VISIBLE
                createOrderBinding.edtLimitPriceTextInputLayout.hint =
                    resources.getString(R.string.trailing_amount)
                createOrderBinding.tifLayout.visibility = View.GONE
                createOrderBinding.checkPostOnly.visibility = View.GONE
                createOrderBinding.edtStopPriceTextInputLayout.visibility = View.GONE
            }
        }

        if (KotlinUtils.apiDetailsPresent(requireContext())) {
            createOrderBinding.buyAndSellSwitch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    createOrderBinding.buyAndSellSwitch.text =
                        requireContext().resources.getString(R.string.buy_long)
                    createOrderBinding.buyAndSellSwitch.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorBid
                        )
                    )
                    createOrderBinding.btnPlaceOrder.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.colorBid)
                } else {
                    createOrderBinding.buyAndSellSwitch.text =
                        requireContext().resources.getString(R.string.sell_short)
                    createOrderBinding.buyAndSellSwitch.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorAsk
                        )
                    )
                    createOrderBinding.btnPlaceOrder.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.colorAsk)
                }
            }
        }

        createOrderBinding.leverageSeeker.onSeekChangeListener = object : OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams) {
                Log.i(logTag, seekParams.seekBar.toString())
                Log.i(logTag, seekParams.progress.toString())
                Log.i(logTag, seekParams.progressFloat.toString())
                Log.i(logTag, seekParams.fromUser.toString())
                //when tick count > 0
                Log.i(logTag, seekParams.thumbPosition.toString())
                Log.i(logTag, seekParams.tickText)
                createOrderBinding.leverageTxt.text = seekParams.tickText
                currentLeverage.text =
                    resources.getString(R.string.current_leverage).plus(seekParams.tickText)

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

                val progressBar = KotlinUtils.showProgressBar(
                    requireActivity(),
                    requireContext().resources.getString(R.string.please_wait)
                )
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
                                errorBody.message!!,
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
            }

            override fun onStartTrackingTouch(seekBar: TickSeekBar?) {}
            override fun onStopTrackingTouch(seekBar: TickSeekBar?) {}
        }

        createOrderBinding.checkPostOnly.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && !createOrderBinding.checkedGTC.isChecked) {
                createOrderBinding.checkPostOnly.isChecked = false
                Toasty.error(
                    requireContext(),
                    requireContext().getString(R.string.post_only_flag_is_for_gtc),
                    Toast.LENGTH_SHORT,
                    true
                ).show()
            }
        }

        createOrderBinding.checkedFOK.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && createOrderBinding.checkPostOnly.isChecked) {
                createOrderBinding.checkPostOnly.isChecked = false
                Toasty.error(
                    requireContext(),
                    requireContext().getString(R.string.post_only_flag_is_for_gtc),
                    Toast.LENGTH_SHORT,
                    true
                ).show()
            }
        }

        createOrderBinding.checkedIOC.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && createOrderBinding.checkPostOnly.isChecked) {
                createOrderBinding.checkPostOnly.isChecked = false
                Toasty.error(
                    requireContext(),
                    requireContext().getString(R.string.post_only_flag_is_for_gtc),
                    Toast.LENGTH_SHORT,
                    true
                ).show()
            }
        }

        createOrderBinding.btnPlaceOrder.setOnClickListener {
            if (createOrderBinding.orderTypeSpinner.selectedItem.toString().equals("Limit", true)) {
                if (createOrderBinding.edtLimitPrice.text!!.isNotEmpty() && createOrderBinding.edtQuantity.text!!.isNotEmpty()) {
                    if (createOrderBinding.edtLimitPrice.text!!.toString()
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
                        createOrderRequest.limitPrice =
                            createOrderBinding.edtLimitPrice.text.toString()
                        createOrderRequest.size =
                            createOrderBinding.edtQuantity.text.toString().toInt()
                        when {
                            createOrderBinding.checkedGTC.isChecked -> {
                                createOrderRequest.timeInForce =
                                    CreateOrderRequest.TimeInForce.gtc
                            }
                            createOrderBinding.checkedFOK.isChecked -> {
                                createOrderRequest.timeInForce =
                                    CreateOrderRequest.TimeInForce.fok
                            }
                            createOrderBinding.checkedIOC.isChecked -> {
                                createOrderRequest.timeInForce =
                                    CreateOrderRequest.TimeInForce.ioc
                            }
                        }
                        if (createOrderBinding.checkPostOnly.isChecked) {
                            createOrderRequest.postOnly = "true"
                        } else {
                            createOrderRequest.postOnly = "false"
                        }
                        if (createOrderBinding.checkReduceOnly.isChecked) {
                            createOrderRequest.reduceOnly = "true"
                        } else {
                            createOrderRequest.reduceOnly = "false"
                        }
                        CoroutineScope(Dispatchers.Main).launch {
                            placeOrder(createOrderRequest, true, createOrderBinding)
                        }
                    } else {
                        if (createOrderBinding.edtLimitPrice.text.toString().toDouble() == 0.0) {
                            Toasty.error(
                                requireContext(),
                                requireContext().getString(R.string.limit_price_is_zero),
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                        }
                        if (createOrderBinding.edtQuantity.text.toString().toDouble() == 0.0) {
                            Toasty.error(
                                requireContext(),
                                requireContext().getString(R.string.quantity_is_zero),
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                        }
                    }
                } else {
                    if (createOrderBinding.edtLimitPrice.text.isNullOrEmpty()) {
                        Toasty.error(
                            requireContext(),
                            requireContext().getString(R.string.limit_price_is_empty),
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                    }
                    if (createOrderBinding.edtQuantity.text.isNullOrEmpty()) {
                        Toasty.error(
                            requireContext(),
                            requireContext().getString(R.string.quantity_is_empty),
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                    }
                }
            } else if (createOrderBinding.orderTypeSpinner.selectedItem.toString()
                    .equals("Market", true)
            ) {
                if (createOrderBinding.edtQuantity.text!!.isNotEmpty()) {
                    if (createOrderBinding.edtQuantity.text!!.toString().toDouble() != 0.0) {
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
                        createOrderRequest.size =
                            createOrderBinding.edtQuantity.text.toString().toInt()
                        if (createOrderBinding.checkReduceOnly.isChecked) {
                            createOrderRequest.reduceOnly = "true"
                        } else {
                            createOrderRequest.reduceOnly = "false"
                        }
                        CoroutineScope(Dispatchers.Main).launch {
                            placeOrder(createOrderRequest, true, createOrderBinding)
                        }
                    } else {
                        if (createOrderBinding.edtQuantity.text.toString().toDouble() == 0.0) {
                            Toasty.error(
                                requireContext(),
                                requireContext().getString(R.string.quantity_is_zero),
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                        }
                    }
                } else {
                    if (createOrderBinding.edtQuantity.text.isNullOrEmpty()) {
                        Toasty.error(
                            requireContext(),
                            requireContext().getString(R.string.quantity_is_empty),
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                    }
                }
            } else if (createOrderBinding.orderTypeSpinner.selectedItem.toString()
                    .equals("Stop Limit", true)
            ) {
                if (createOrderBinding.edtLimitPrice.text!!.isNotEmpty() && createOrderBinding.edtQuantity.text!!.isNotEmpty()) {
                    if (createOrderBinding.edtLimitPrice.text!!.toString()
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
                        createOrderRequest.limitPrice =
                            createOrderBinding.edtLimitPrice.text.toString()
                        createOrderRequest.stopPrice =
                            createOrderBinding.edtStopPrice.text.toString()
                        createOrderRequest.size = edtQuantity.text.toString().toInt()
                        createOrderRequest.stopOrderType = "stop_loss_order"
                        if (createOrderBinding.checkReduceOnly.isChecked) {
                            createOrderRequest.reduceOnly = "true"
                        } else {
                            createOrderRequest.reduceOnly = "false"
                        }
                        CoroutineScope(Dispatchers.Main).launch {
                            placeOrder(createOrderRequest, true, createOrderBinding)
                        }
                    } else {
                        if (createOrderBinding.edtLimitPrice.text.toString().toDouble() == 0.0) {
                            Toasty.error(
                                requireContext(),
                                requireContext().getString(R.string.limit_price_is_zero),
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                        }
                        if (createOrderBinding.edtStopPrice.text.toString().toDouble() == 0.0) {
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
                    if (createOrderBinding.edtLimitPrice.text.isNullOrEmpty()) {
                        Toasty.error(
                            requireContext(),
                            requireContext().getString(R.string.limit_price_is_empty),
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                    }
                    if (createOrderBinding.edtStopPrice.text.isNullOrEmpty()) {
                        Toasty.error(
                            requireContext(),
                            requireContext().getString(R.string.stop_price_is_empty),
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                    }
                    if (createOrderBinding.edtQuantity.text.isNullOrEmpty()) {
                        Toasty.error(
                            requireContext(),
                            requireContext().getString(R.string.quantity_is_empty),
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                    }
                }
            } else if (createOrderBinding.orderTypeSpinner.selectedItem.toString()
                    .equals("Stop Market", true)
            ) {
                if (createOrderBinding.edtStopPrice.text!!.isNotEmpty() && createOrderBinding.edtQuantity.text!!.isNotEmpty()) {
                    if (createOrderBinding.edtLimitPrice.text!!.toString()
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
                        createOrderRequest.stopPrice =
                            createOrderBinding.edtStopPrice.text.toString()
                        createOrderRequest.size = edtQuantity.text.toString().toInt()
                        createOrderRequest.closeOnTrigger = "false"
                        createOrderRequest.stopOrderType = "stop_loss_order"
                        if (createOrderBinding.checkReduceOnly.isChecked) {
                            createOrderRequest.reduceOnly = "true"
                        } else {
                            createOrderRequest.reduceOnly = "false"
                        }
                        CoroutineScope(Dispatchers.Main).launch {
                            placeOrder(createOrderRequest, true, createOrderBinding)
                        }
                    } else {
                        if (createOrderBinding.edtStopPrice.text.toString().toDouble() == 0.0) {
                            Toasty.error(
                                requireContext(),
                                requireContext().getString(R.string.stop_price_is_zero),
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                        }
                        if (createOrderBinding.edtQuantity.text.toString().toDouble() == 0.0) {
                            Toasty.error(
                                requireContext(),
                                requireContext().getString(R.string.quantity_is_zero),
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                        }
                    }
                } else {
                    if (createOrderBinding.edtStopPrice.text.isNullOrEmpty()) {
                        Toasty.error(
                            requireContext(),
                            requireContext().getString(R.string.stop_price_is_empty),
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                    }
                    if (createOrderBinding.edtQuantity.text.isNullOrEmpty()) {
                        Toasty.error(
                            requireContext(),
                            requireContext().getString(R.string.quantity_is_empty),
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                    }
                }
            } else if (createOrderBinding.orderTypeSpinner.selectedItem.toString()
                    .equals("Trailing Stop", true)
            ) {
                if (createOrderBinding.edtLimitPrice.text!!.isNotEmpty() && createOrderBinding.edtQuantity.text!!.isNotEmpty()) {
                    if (createOrderBinding.edtLimitPrice.text!!.toString()
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
                        createOrderRequest.trailAmount =
                            createOrderBinding.edtLimitPrice.text.toString()
                        createOrderRequest.size = edtQuantity.text.toString().toInt()
                        createOrderRequest.closeOnTrigger = "false"
                        createOrderRequest.stopOrderType = "stop_loss_order"
                        if (createOrderBinding.checkReduceOnly.isChecked) {
                            createOrderRequest.reduceOnly = "true"
                        } else {
                            createOrderRequest.reduceOnly = "false"
                        }
                        CoroutineScope(Dispatchers.Main).launch {
                            placeOrder(createOrderRequest, true, createOrderBinding)
                        }
                    } else {
                        if (createOrderBinding.edtLimitPrice.text.toString().toDouble() == 0.0) {
                            Toasty.error(
                                requireContext(),
                                requireContext().getString(R.string.trailing_amount_is_zero),
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                        }
                        if (createOrderBinding.edtQuantity.text.toString().toDouble() == 0.0) {
                            Toasty.error(
                                requireContext(),
                                requireContext().getString(R.string.quantity_is_zero),
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                        }
                    }
                } else {
                    if (createOrderBinding.edtLimitPrice.text.isNullOrEmpty()) {
                        Toasty.error(
                            requireContext(),
                            requireContext().getString(R.string.trailing_amount_is_empty),
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                    }
                    if (createOrderBinding.edtQuantity.text.isNullOrEmpty()) {
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
    }

    private suspend fun placeOrder(
        createOrderRequest: CreateOrderRequest,
        isDialog: Boolean,
        createOrderBinding: CreateOrderBinding?
    ) {
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
                        if (isDialog) {
                            createOrderBinding!!.edtLimitPrice.editableText.clear()
                            createOrderBinding.edtStopPrice.editableText.clear()
                            createOrderBinding.edtQuantity.editableText.clear()
                        } else {
                            //edtLimitPrice.editableText.clear()
                            edtPrice.editableText.clear()
                            edtQuantity.editableText.clear()
                        }
                    } else {
                        val errorBody = Gson().fromJson(
                            response.errorBody()!!.charStream(),
                            ErrorResponse::class.java
                        )
                        Toasty.error(
                            requireContext(),
                            errorBody.message!!,
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
                2 -> {
                    // find second fragment...
                    StopOrdersFragment()
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
            if (`object` is StopOrdersFragment) {
                `object`.update()
            }
            return super.getItemPosition(`object`)
        }

        override fun getCount(): Int {
            // Show 2 total pages.
            return 3
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 -> return resources.getString(R.string.position)
                1 -> return resources.getString(R.string.open_orders)
                2 -> return resources.getString(R.string.stop_orders)
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