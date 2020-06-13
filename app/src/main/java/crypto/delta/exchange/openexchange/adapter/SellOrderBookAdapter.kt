package crypto.delta.exchange.openexchange.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import crypto.delta.exchange.openexchange.R
import crypto.delta.exchange.openexchange.pojo.Sell

class SellOrderBookAdapter(private var sellList: List<Sell>) : RecyclerView.Adapter<SellOrderBookAdapter.ViewHolder>() {

    override fun getItemCount() = sellList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_sell_order_book, parent, false)
        return ViewHolder(v)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val sellSize: AppCompatTextView = itemView.findViewById(R.id.sellSize)
        internal val sellPrice: AppCompatTextView = itemView.findViewById(R.id.sellPrice)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val buy = sellList[position]
        holder.sellSize.text = buy.d_size.toString()
        holder.sellPrice.text = buy.limitPrice?.toBigDecimal()?.toPlainString()
    }

    fun updateOrderBook(orderBook: List<Sell>) {
        val diffResult =
            DiffUtil.calculateDiff(OrderBookDiffCallback(this.sellList, orderBook), true)

        this.sellList = orderBook

        diffResult.dispatchUpdatesTo(this)
    }

    private class OrderBookDiffCallback(
        private val oldPriceOrderBook: List<Sell>,
        private val newPriceOrderBook: List<Sell>
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
