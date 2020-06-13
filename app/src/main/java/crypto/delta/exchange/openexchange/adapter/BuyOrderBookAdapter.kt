package crypto.delta.exchange.openexchange.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import crypto.delta.exchange.openexchange.R
import crypto.delta.exchange.openexchange.pojo.Buy

class BuyOrderBookAdapter(private var buyList: List<Buy>) : RecyclerView.Adapter<BuyOrderBookAdapter.ViewHolder>() {

    override fun getItemCount() = buyList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_buy_order_book, parent, false)
        return ViewHolder(v)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val sizeBid: AppCompatTextView = itemView.findViewById(R.id.bidSize)
        internal val priceBid: AppCompatTextView = itemView.findViewById(R.id.bidPrice)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val buy = buyList[position]
        holder.sizeBid.text = buy.d_size.toString()
        holder.priceBid.text = buy.limitPrice?.toBigDecimal()?.toPlainString()
    }

    fun updateOrderBook(orderBook: List<Buy>) {
        val diffResult =
            DiffUtil.calculateDiff(OrderBookDiffCallback(this.buyList, orderBook), true)

        this.buyList = orderBook

        diffResult.dispatchUpdatesTo(this)
    }

    private class OrderBookDiffCallback(
        private val oldPriceOrderBook: List<Buy>,
        private val newPriceOrderBook: List<Buy>
    ) : DiffUtil.Callback() {
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return this.newPriceOrderBook == this.oldPriceOrderBook
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return this.newPriceOrderBook.size == this.oldPriceOrderBook.size
        }

        override fun getNewListSize() = this.newPriceOrderBook.size

        override fun getOldListSize() = this.oldPriceOrderBook.size
    }
}
