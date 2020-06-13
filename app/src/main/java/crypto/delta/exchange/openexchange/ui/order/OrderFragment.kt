package crypto.delta.exchange.openexchange.ui.order

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.warkiz.tickseekbar.OnSeekChangeListener
import com.warkiz.tickseekbar.SeekParams
import com.warkiz.tickseekbar.TickSeekBar
import crypto.delta.exchange.openexchange.R
import crypto.delta.exchange.openexchange.adapter.BuyOrderBookAdapter
import crypto.delta.exchange.openexchange.adapter.SellOrderBookAdapter
import crypto.delta.exchange.openexchange.api.NoConnectivityException
import crypto.delta.exchange.openexchange.pojo.Buy
import crypto.delta.exchange.openexchange.pojo.Sell
import crypto.delta.exchange.openexchange.pojo.order.ChangeOrderLeverageBody
import crypto.delta.exchange.openexchange.pojo.order.CreateOrderRequest
import crypto.delta.exchange.openexchange.pojo.order.CreateOrderResponse
import crypto.delta.exchange.openexchange.pojo.order.OrderLeverageResponse
import crypto.delta.exchange.openexchange.ui.base.BaseFragment
import crypto.delta.exchange.openexchange.utils.KotlinUtils
import es.dmoral.toasty.Toasty
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_order.*
import kotlinx.coroutines.*
import java.net.ConnectException
import java.net.SocketTimeoutException

class OrderFragment : BaseFragment() {
    private val logTag: String? = OrderFragment::class.java.simpleName
    private val disposables: CompositeDisposable = CompositeDisposable()
    private var buyOrderBookAdapter: BuyOrderBookAdapter? = null
    private var sellOrderBookAdapter: SellOrderBookAdapter? = null
    private var buyOrderBookList: List<Buy> = ArrayList()
    private var sellOrderBookList: List<Sell> = ArrayList()
    private var buyLayoutManager: LinearLayoutManager? = null
    private var sellLayoutManager: LinearLayoutManager? = null
    private lateinit var orderBookViewModel: OrderBookViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sectionsPagerAdapter = SectionsPagerAdapter(childFragmentManager)
        viewPager!!.adapter = sectionsPagerAdapter
        viewPager!!.currentItem = 0

        orderPositionTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
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

        CoroutineScope(Dispatchers.Main).launch {
            delay(500)
            orderBookViewModel = ViewModelProvider(this@OrderFragment).get(OrderBookViewModel::class.java)
            orderBookViewModel.init(requireContext())
            orderBookViewModel.observeWebSocketEvent()
            orderBookViewModel.observeOrderBook().observe(viewLifecycleOwner, Observer {
                if (null != it) {
                    buyOrderBookAdapter!!.updateOrderBook(it.buy!!)
                    sellOrderBookAdapter!!.updateOrderBook(it.sell!!)
                    if (buyLayoutManager!!.findFirstVisibleItemPosition() == 0) {
                        buyLayoutManager!!.scrollToPositionWithOffset(0, 0)
                    }
                    if (sellLayoutManager!!.findFirstVisibleItemPosition() == 0) {
                        sellLayoutManager!!.scrollToPositionWithOffset(0, 0)
                    }
                }
            })
        }

        var leverageChangedByUser = false
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

        val progressBar = KotlinUtils.showProgressBar(
            requireActivity(),
            requireContext().resources.getString(R.string.please_wait)
        )
        val observe = service!!.getOrderLeverage(
            appPreferenceManager!!.apiKey!!,
            timeStamp,
            signature!!,
            appPreferenceManager!!.currentProductId!!
        )
        observe.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<OrderLeverageResponse>() {
                override fun onComplete() {
                    Log.d("getOrderLeverage", "onComplete")
                    progressBar.dismiss()
                }

                override fun onNext(responseData: OrderLeverageResponse) {
                    Log.d(logTag, responseData.leverage)
                    when {
                        responseData.leverage.toDouble() == 1.0 -> {
                            leverageSeeker.setProgress(0f)
                        }
                        responseData.leverage.toDouble() == 2.0 -> {
                            leverageSeeker.setProgress(14f)
                        }
                        responseData.leverage.toDouble() == 3.0 -> {
                            leverageSeeker.setProgress(29f)
                        }
                        responseData.leverage.toDouble() == 5.0 -> {
                            leverageSeeker.setProgress(43f)
                        }
                        responseData.leverage.toDouble() == 10.0 -> {
                            leverageSeeker.setProgress(57f)
                        }
                        responseData.leverage.toDouble() == 25.0 -> {
                            leverageSeeker.setProgress(71f)
                        }
                        responseData.leverage.toDouble() == 50.0 -> {
                            leverageSeeker.setProgress(86f)
                        }
                        responseData.leverage.toDouble() == 100.0 -> {
                            leverageSeeker.setProgress(100f)
                        }
                    }

                    leverageTxt.text =
                        responseData.leverage.toDouble().toInt().toString().plus("x")
                    leverageChangedByUser = true
                }

                override fun onError(error: Throwable) {
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
                            error.printStackTrace()
                        }
                    }
                    error.printStackTrace()
                    progressBar.dismiss()
                    leverageChangedByUser = true
                }
            }).addTo(disposables)


        orderTypeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.checkedLimit -> {
                    limitPriceTextInputLayout.visibility = View.VISIBLE
                    tifLayout.visibility = View.VISIBLE
                    checkPostOnly.visibility = View.VISIBLE
                }
                R.id.checkedMarket -> {
                    limitPriceTextInputLayout.visibility = View.GONE
                    tifLayout.visibility = View.GONE
                    checkPostOnly.visibility = View.GONE
                }
            }
        }

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
                    Log.d(logTag, signatureData)
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
                    observeOrderLeverage.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object :
                            DisposableObserver<OrderLeverageResponse>() {
                            override fun onComplete() {
                                Log.d("getOrderLeverage", "onComplete")
                                progressBar.dismiss()
                            }

                            override fun onNext(responseData: OrderLeverageResponse) {
                                Log.d(logTag, responseData.leverage)
                            }

                            override fun onError(error: Throwable) {
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
                                        error.printStackTrace()
                                    }
                                }
                                error.printStackTrace()
                                progressBar.dismiss()
                            }
                        }).addTo(disposables)
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
            if (checkedLimit.isChecked) {
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
            } else if (checkedMarket.isChecked) {
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
            }
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
            observe.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<CreateOrderResponse>() {
                    override fun onComplete() {
                        Log.d("placeOrder", "onComplete")
                    }

                    override fun onNext(responseData: CreateOrderResponse) {
                        Toasty.success(
                            requireContext(),
                            responseData.orderType!!.replace("_", "").plus(" successfully placed"),
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                    }

                    override fun onError(error: Throwable) {
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
                            }
                        }
                        error.printStackTrace()
                    }
                }).addTo(disposables)
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
                    Fragment()
                }
                1 -> {
                    // find second fragment...
                    OpenOrdersFragment()
                }
                else -> Fragment()
            }
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
}