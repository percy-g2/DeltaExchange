package crypto.delta.exchange.openexchange.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import crypto.delta.exchange.openexchange.R
import crypto.delta.exchange.openexchange.adapter.WalletAdapter
import crypto.delta.exchange.openexchange.pojo.ErrorResponse
import crypto.delta.exchange.openexchange.pojo.WalletResponse
import crypto.delta.exchange.openexchange.ui.base.BaseFragment
import crypto.delta.exchange.openexchange.utils.KotlinUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_wallet.*


class WalletFragment : BaseFragment() {
    private lateinit var walletViewModel: WalletViewModel
    private var walletResponseList: ArrayList<WalletResponse>? = null
    private var walletAdapter: WalletAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        walletViewModel = ViewModelProvider(this@WalletFragment).get(WalletViewModel::class.java)
        walletViewModel.init()
        return inflater.inflate(R.layout.fragment_wallet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        walletResponseList = ArrayList()
        walletRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        walletRecyclerView.itemAnimator = null
        walletRecyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        walletAdapter = WalletAdapter(walletResponseList!!)
        walletRecyclerView.adapter = walletAdapter

        val timeStamp = KotlinUtils.generateTimeStamp()
        val method = "GET"
        val path = "/wallet/balances"
        val queryString = ""
        val payload = ""
        val signatureData = method + timeStamp + path + queryString + payload
        val signature = KotlinUtils.generateSignature(
            signatureData,
            appPreferenceManager!!.apiSecret!!
        )
        walletViewModel.getWallet(appPreferenceManager!!.apiKey!!, timeStamp, signature!!)!!
            .observe(viewLifecycleOwner, Observer {
                if (it.code() == 200) {
                    val myType = object : TypeToken<List<WalletResponse>>() {}.type
                    val responseList =
                        Gson().fromJson<List<WalletResponse>>(it.body()!!.charStream(), myType)
                    walletResponseList!!.clear()
                    walletResponseList!!.addAll(responseList)
                    walletAdapter!!.notifyDataSetChanged()
                    errorImage.visibility = View.GONE
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
                    errorImage.visibility = View.VISIBLE
                }
                progressSpinner.visibility = View.GONE
            })
    }
}