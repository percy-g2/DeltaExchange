package crypto.delta.exchange.openexchange.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import crypto.delta.exchange.openexchange.R
import crypto.delta.exchange.openexchange.pojo.RecentTrade
import java.text.SimpleDateFormat
import java.util.*

class RecentTradesAdapter (private var recentTradesList: ArrayList<RecentTrade>) : RecyclerView.Adapter<RecentTradesAdapter.ViewHolder>() {

    override fun getItemCount() = recentTradesList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_recent_trades, parent, false)
        return ViewHolder(v)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal val tradePrice: AppCompatTextView = itemView.findViewById(R.id.tradePrice)
        internal val tradeSize: AppCompatTextView = itemView.findViewById(R.id.tradeSize)
        internal val tradeTime: AppCompatTextView = itemView.findViewById(R.id.tradeTime)
        internal val tradeTaker: AppCompatTextView = itemView.findViewById(R.id.tradeTaker)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recentTrade = recentTradesList[position]

        holder.tradePrice.text = recentTrade.price.toString()
        holder.tradeSize.text = recentTrade.size.toString()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mma", Locale.ENGLISH)
        holder.tradeTime.text = simpleDateFormat.format(Date(recentTrade.timestamp!! / 1000))
        if (recentTrade.sellerRole.equals("taker", true)) {
            holder.tradeTaker.text = "S"
        } else if (recentTrade.buyerRole.equals("taker", true)) {
            holder.tradeTaker.text = "B"
        }

        if (position != 0) {
            val recentPreviousTradePrice = recentTradesList[position - 1].price!!.toDouble()
            val currentPrice = recentTrade.price!!.toDouble()
            val diff = currentPrice - recentPreviousTradePrice
            Log.d("previousPrice", recentPreviousTradePrice.toString())
            Log.d("currentPrice", currentPrice.toString())
            Log.d("diff", diff.toString())
        }
    }
}
