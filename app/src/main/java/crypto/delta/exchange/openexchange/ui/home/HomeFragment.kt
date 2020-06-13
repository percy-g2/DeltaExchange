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
import crypto.delta.exchange.openexchange.pojo.products.ProductsResponse
import crypto.delta.exchange.openexchange.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var homeViewModel: HomeViewModel

    private var productsResponseList: List<ProductsResponse>? = null
    private var filteredProductsResponseList: List<ProductsResponse>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this@HomeFragment).get(HomeViewModel::class.java)
        homeViewModel.init(requireContext())
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        homeRecyclerView.itemAnimator = null
        homeRecyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        swipeLayout.setOnRefreshListener(this)

        homeViewModel.getProducts()!!.observe(viewLifecycleOwner, Observer { list ->
            if (null != list) {
                productsResponseList = list
                filteredProductsResponseList = productsResponseList
                homeRecyclerView.adapter = HomeAdapter(
                    filteredProductsResponseList!!.filter {
                        (it.productType == "inverse_future" || it.productType == "future") && (it.contractType == "futures" || it.contractType == "perpetual_futures") && !it.symbol!!.contains(
                            "USDT".toRegex()
                        )
                    },
                    requireActivity()
                )
                progressSpinner.visibility = View.GONE
            }
        })


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
                                requireActivity()
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
                                requireActivity()
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
                                        requireActivity()
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
                                        requireActivity()
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
                                requireActivity()
                            )
                        }
                    }
                    tab.text.toString() == resources.getString(R.string.interest_rate_swaps) -> {
                        futuresSettled.visibility = View.GONE
                        if (null != productsResponseList) {
                            filteredProductsResponseList = productsResponseList
                            homeRecyclerView.adapter = HomeAdapter(
                                filteredProductsResponseList!!.filter { it.productType == "inverse_future" && it.contractType == "interest_rate_swaps" },
                                requireActivity()
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
                                        requireActivity()
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
                                        requireActivity()
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
                                requireActivity()
                            )
                        }
                    }
                    tab.text.toString() == resources.getString(R.string.interest_rate_swaps) -> {
                        futuresSettled.visibility = View.GONE
                        if (null != productsResponseList) {
                            filteredProductsResponseList = productsResponseList
                            homeRecyclerView.adapter = HomeAdapter(
                                filteredProductsResponseList!!.filter { it.productType == "inverse_future" && it.contractType == "interest_rate_swaps" },
                                requireActivity()
                            )
                        }
                    }
                }

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }
        })

    }

    override fun onRefresh() {

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
                                        requireActivity()
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
                                        requireActivity()
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
                                requireActivity()
                            )
                        }
                    }
                    productType.getTabAt(productType.selectedTabPosition)!!.text.toString() == resources.getString(
                        R.string.interest_rate_swaps
                    ) -> {
                        if (null != productsResponseList) {
                            homeRecyclerView.adapter = HomeAdapter(
                                filteredProductsResponseList!!.filter { it.productType == "inverse_future" && it.contractType == "interest_rate_swaps" },
                                requireActivity()
                            )
                        }
                    }
                }
            }

            if (swipeLayout.isRefreshing) {
                swipeLayout.isRefreshing = false
            }
        })
    }
}