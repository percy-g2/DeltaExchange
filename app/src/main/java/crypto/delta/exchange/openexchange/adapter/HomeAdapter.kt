package crypto.delta.exchange.openexchange.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import crypto.delta.exchange.openexchange.R
import crypto.delta.exchange.openexchange.pojo.DeltaExchangeTickerResponse
import crypto.delta.exchange.openexchange.pojo.products.ProductsResponse
import crypto.delta.exchange.openexchange.utils.AppPreferenceManager


class HomeAdapter(
    private var productsResponseList: List<ProductsResponse>,
    private val requireActivity: FragmentActivity,
    private var tickerResponseList: HashSet<DeltaExchangeTickerResponse>?
) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    override fun getItemCount() = productsResponseList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_home, parent, false)
        return ViewHolder(v)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val symbol: AppCompatTextView = itemView.findViewById(R.id.symbol)
        internal val lastPrice: AppCompatTextView = itemView.findViewById(R.id.lastPrice)
        internal val dayVolume: AppCompatTextView = itemView.findViewById(R.id.dayVolume)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productsResponseList[position]
        holder.symbol.text = product.symbol
        holder.itemView.setOnClickListener {
            AppPreferenceManager(requireActivity).setCurrentProductSymbol(product.symbol)
            AppPreferenceManager(requireActivity).setCurrentProductId(product.id.toString())
            requireActivity.findNavController(R.id.nav_host_fragment)
                .navigate(R.id.action_to_chart)
        }
        for (ticker in tickerResponseList!!) {
            if (ticker.symbol == product.symbol) {
                holder.lastPrice.text = ticker.close?.toBigDecimal()?.toPlainString()
                holder.dayVolume.text =
                    String.format(ticker.volume!!.toString() + " " + product.quotingAsset!!.symbol)
            }
        }
    }

    fun updateData(data: List<ProductsResponse>) {
        this.productsResponseList = data
        notifyDataSetChanged()
    }

    fun updateTicker(deltaExchangeTickerResponse: DeltaExchangeTickerResponse) {
        for (ticker in this.tickerResponseList!!) {
            if (ticker.symbol == deltaExchangeTickerResponse.symbol) {
                tickerResponseList!!.remove(ticker)
            }
        }
    }
}
