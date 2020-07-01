package crypto.delta.exchange.openexchange.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.tabs.TabLayout
import crypto.delta.exchange.openexchange.R
import crypto.delta.exchange.openexchange.adapter.HomeAdapter
import crypto.delta.exchange.openexchange.pojo.DeltaExchangeTickerResponse
import crypto.delta.exchange.openexchange.pojo.products.ProductsResponse
import crypto.delta.exchange.openexchange.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var homeViewModel: HomeViewModel

    private var productsResponseList: List<ProductsResponse> = ArrayList()
    private var filteredProductsResponseList: List<ProductsResponse>? = ArrayList()
    private var tickerResponseList: HashSet<DeltaExchangeTickerResponse> = HashSet()
    private var layoutManager: LinearLayoutManager? = null
    private var homeAdapter: HomeAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this@HomeFragment).get(HomeViewModel::class.java)
        homeViewModel.init()
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutManager = LinearLayoutManager(requireContext())
        homeRecyclerView.layoutManager = layoutManager
        homeRecyclerView.itemAnimator = null
        homeRecyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        swipeLayout.setOnRefreshListener(this)

        homeAdapter = HomeAdapter(productsResponseList, requireActivity(), tickerResponseList)
        homeRecyclerView.adapter = homeAdapter
        homeViewModel.getProducts()!!.observe(viewLifecycleOwner, Observer { list ->
            if (null != list) {
                productsResponseList = list
                filteredProductsResponseList = productsResponseList
                homeViewModel.observeWebSocketEvent(productsResponseList)
                homeAdapter!!.updateData(filteredProductsResponseList!!.filter {
                    (it.productType == "inverse_future" || it.productType == "future") && (it.contractType == "futures" || it.contractType == "perpetual_futures") && !it.symbol!!.contains(
                        "USDT".toRegex()
                    )
                })
                homeViewModel.observeTicker()
                    .observe(viewLifecycleOwner, Observer { deltaExchangeTickerResponse ->
                        homeAdapter!!.updateTicker(deltaExchangeTickerResponse)
                        if (layoutManager!!.findFirstVisibleItemPosition() == 0) {
                            layoutManager!!.scrollToPositionWithOffset(0, 0)
                        } else {
                            homeRecyclerView.scrollToPosition(layoutManager!!.findLastVisibleItemPosition() - 1)
                        }
                    })
                progressSpinner.visibility = View.GONE
                errorImage.visibility = View.GONE
            } else {
                errorImage.visibility = View.VISIBLE
                progressSpinner.visibility = View.GONE
            }
        })

        errorImage.setOnClickListener {
            loadAgain()
        }

        futuresSettled.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when {
                    tab!!.text.toString() == resources.getString(R.string.btc_settled) -> {
                        if (null != productsResponseList) {
                            filteredProductsResponseList = productsResponseList
                            homeRecyclerView.adapter = HomeAdapter(
                                filteredProductsResponseList!!.filter {
                                    (it.productType == "inverse_future" || it.productType == "future") && (it.contractType == "futures" || it.contractType == "perpetual_futures") && !it.symbol!!.contains(
                                        "USDT".toRegex()
                                    )
                                },
                                requireActivity(),
                                tickerResponseList
                            )
                        }
                    }
                    tab.text.toString() == resources.getString(R.string.usdt_settled) -> {
                        if (null != productsResponseList) {
                            filteredProductsResponseList = productsResponseList
                            homeRecyclerView.adapter = HomeAdapter(
                                filteredProductsResponseList!!.filter {
                                    (it.productType == "inverse_future" || it.productType == "future") && (it.contractType == "futures" || it.contractType == "perpetual_futures") && it.symbol!!.contains(
                                        "USDT".toRegex()
                                    )
                                },
                                requireActivity(),
                                tickerResponseList
                            )
                        }
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }
        })


        productType.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when {
                    tab!!.text.toString() == resources.getString(R.string.futures) -> {
                        futuresSettled.visibility = View.VISIBLE
                        when {
                            futuresSettled.getTabAt(futuresSettled.selectedTabPosition)!!.text.toString() == resources.getString(
                                R.string.btc_settled
                            ) -> {
                                if (null != productsResponseList) {
                                    filteredProductsResponseList = productsResponseList
                                    homeRecyclerView.adapter = HomeAdapter(
                                        filteredProductsResponseList!!.filter {
                                            (it.productType == "inverse_future" || it.productType == "future") && (it.contractType == "futures" || it.contractType == "perpetual_futures") && !it.symbol!!.contains(
                                                "USDT".toRegex()
                                            )
                                        },
                                        requireActivity(),
                                        tickerResponseList
                                    )
                                }
                            }
                            futuresSettled.getTabAt(futuresSettled.selectedTabPosition)!!.text.toString() == resources.getString(
                                R.string.usdt_settled
                            ) -> {
                                if (null != productsResponseList) {
                                    filteredProductsResponseList = productsResponseList
                                    homeRecyclerView.adapter = HomeAdapter(
                                        filteredProductsResponseList!!.filter {
                                            (it.productType == "inverse_future" || it.productType == "future") && (it.contractType == "futures" || it.contractType == "perpetual_futures") && it.symbol!!.contains(
                                                "USDT".toRegex()
                                            )
                                        },
                                        requireActivity(),
                                        tickerResponseList
                                    )
                                }
                            }
                        }
                    }
                    tab.text.toString() == resources.getString(R.string.move) -> {
                        futuresSettled.visibility = View.GONE
                        if (null != productsResponseList) {
                            filteredProductsResponseList = productsResponseList
                            homeRecyclerView.adapter = HomeAdapter(
                                filteredProductsResponseList!!.filter { it.productType == "future" && it.contractType == "move_options" },
                                requireActivity(),
                                tickerResponseList
                            )
                        }
                    }
                    tab.text.toString() == resources.getString(R.string.interest_rate_swaps) -> {
                        futuresSettled.visibility = View.GONE
                        if (null != productsResponseList) {
                            filteredProductsResponseList = productsResponseList
                            homeRecyclerView.adapter = HomeAdapter(
                                filteredProductsResponseList!!.filter { it.productType == "inverse_future" && it.contractType == "interest_rate_swaps" },
                                requireActivity(),
                                tickerResponseList
                            )
                        }
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                when {
                    tab!!.text.toString() == resources.getString(R.string.futures) -> {
                        futuresSettled.visibility = View.VISIBLE
                        when {
                            futuresSettled.getTabAt(futuresSettled.selectedTabPosition)!!.text.toString() == resources.getString(
                                R.string.btc_settled
                            ) -> {
                                if (null != productsResponseList) {
                                    filteredProductsResponseList = productsResponseList
                                    homeRecyclerView.adapter = HomeAdapter(
                                        filteredProductsResponseList!!.filter {
                                            (it.productType == "inverse_future" || it.productType == "future") && (it.contractType == "futures" || it.contractType == "perpetual_futures") && !it.symbol!!.contains(
                                                "USDT".toRegex()
                                            )
                                        },
                                        requireActivity(),
                                        tickerResponseList
                                    )
                                }
                            }
                            futuresSettled.getTabAt(futuresSettled.selectedTabPosition)!!.text.toString() == resources.getString(
                                R.string.usdt_settled
                            ) -> {
                                if (null != productsResponseList) {
                                    filteredProductsResponseList = productsResponseList
                                    homeRecyclerView.adapter = HomeAdapter(
                                        filteredProductsResponseList!!.filter {
                                            (it.productType == "inverse_future" || it.productType == "future") && (it.contractType == "futures" || it.contractType == "perpetual_futures") && it.symbol!!.contains(
                                                "USDT".toRegex()
                                            )
                                        },
                                        requireActivity(),
                                        tickerResponseList
                                    )
                                }
                            }
                        }
                    }
                    tab.text.toString() == resources.getString(R.string.move) -> {
                        futuresSettled.visibility = View.GONE
                        if (null != productsResponseList) {
                            filteredProductsResponseList = productsResponseList
                            homeRecyclerView.adapter = HomeAdapter(
                                filteredProductsResponseList!!.filter { it.productType == "future" && it.contractType == "move_options" },
                                requireActivity(),
                                tickerResponseList
                            )
                        }
                    }
                    tab.text.toString() == resources.getString(R.string.interest_rate_swaps) -> {
                        futuresSettled.visibility = View.GONE
                        if (null != productsResponseList) {
                            filteredProductsResponseList = productsResponseList
                            homeRecyclerView.adapter = HomeAdapter(
                                filteredProductsResponseList!!.filter { it.productType == "inverse_future" && it.contractType == "interest_rate_swaps" },
                                requireActivity(),
                                tickerResponseList
                            )
                        }
                    }
                }

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }
        })

    }

    private fun loadAgain() {

        homeViewModel.getProducts()!!.observe(viewLifecycleOwner, Observer { list ->
            if (null != list) {
                productsResponseList = list
                filteredProductsResponseList = productsResponseList
                when {
                    productType.getTabAt(productType.selectedTabPosition)!!.text.toString() == resources.getString(
                        R.string.futures
                    ) -> {
                        when {
                            futuresSettled.getTabAt(futuresSettled.selectedTabPosition)!!.text.toString() == resources.getString(
                                R.string.btc_settled
                            ) -> {
                                if (null != productsResponseList) {
                                    filteredProductsResponseList = productsResponseList
                                    homeRecyclerView.adapter = HomeAdapter(
                                        filteredProductsResponseList!!.filter {
                                            (it.productType == "inverse_future" || it.productType == "future") && (it.contractType == "futures" || it.contractType == "perpetual_futures") && !it.symbol!!.contains(
                                                "USDT".toRegex()
                                            )
                                        },
                                        requireActivity(),
                                        tickerResponseList
                                    )
                                }
                            }
                            futuresSettled.getTabAt(futuresSettled.selectedTabPosition)!!.text.toString() == resources.getString(
                                R.string.usdt_settled
                            ) -> {
                                if (null != productsResponseList) {
                                    filteredProductsResponseList = productsResponseList
                                    homeRecyclerView.adapter = HomeAdapter(
                                        filteredProductsResponseList!!.filter {
                                            (it.productType == "inverse_future" || it.productType == "future") && (it.contractType == "futures" || it.contractType == "perpetual_futures") && it.symbol!!.contains(
                                                "USDT".toRegex()
                                            )
                                        },
                                        requireActivity(),
                                        tickerResponseList
                                    )
                                }
                            }
                        }
                    }
                    productType.getTabAt(productType.selectedTabPosition)!!.text.toString() == resources.getString(
                        R.string.move
                    ) -> {
                        if (null != productsResponseList) {
                            homeRecyclerView.adapter = HomeAdapter(
                                filteredProductsResponseList!!.filter { it.productType == "future" && it.contractType == "move_options" },
                                requireActivity(),
                                tickerResponseList
                            )
                        }
                    }
                    productType.getTabAt(productType.selectedTabPosition)!!.text.toString() == resources.getString(
                        R.string.interest_rate_swaps
                    ) -> {
                        if (null != productsResponseList) {
                            homeRecyclerView.adapter = HomeAdapter(
                                filteredProductsResponseList!!.filter { it.productType == "inverse_future" && it.contractType == "interest_rate_swaps" },
                                requireActivity(),
                                tickerResponseList
                            )
                        }
                    }
                }
            }

            if (swipeLayout.isRefreshing) {
                swipeLayout.isRefreshing = false
            }
            progressSpinner.visibility = View.GONE
            errorImage.visibility = View.GONE
        })
    }

    override fun onRefresh() {
        loadAgain()
    }
}