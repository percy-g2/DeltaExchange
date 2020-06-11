package crypto.delta.exchange.openexchange.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import crypto.delta.exchange.openexchange.R
import crypto.delta.exchange.openexchange.pojo.DeltaExchangeL2OrderBookResponse

class OrderBookAdapter(private var orderBookList: DeltaExchangeL2OrderBookResponse) : RecyclerView.Adapter<OrderBookAdapter.ViewHolder>() {

    override fun getItemCount() = orderBookList.buy!!.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_order_book, parent, false)
        return ViewHolder(v)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal val sizeBid: AppCompatTextView = itemView.findViewById(R.id.size_bid)
        internal val priceBid: AppCompatTextView = itemView.findViewById(R.id.price_bid)
        internal val sizeAsk: AppCompatTextView = itemView.findViewById(R.id.size_ask)
        internal val priceAsk: AppCompatTextView = itemView.findViewById(R.id.price_ask)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val buy = orderBookList.buy!![position]
        val ask = orderBookList.sell!![position]

        holder.sizeBid.text = buy.d_size.toString()
        holder.priceBid.text = buy.limitPrice.toString()
        holder.sizeAsk.text = ask.d_size.toString()
        holder.priceAsk.text = ask.limitPrice.toString()
    }

    fun updateOrderBook(orderBook: DeltaExchangeL2OrderBookResponse) {
        val diffResult =
            DiffUtil.calculateDiff(OrderBookDiffCallback(this.orderBookList, orderBook), true)

        this.orderBookList = orderBook

        diffResult.dispatchUpdatesTo(this)
    }

    private class OrderBookDiffCallback(
        private val oldPriceOrderBook: DeltaExchangeL2OrderBookResponse,
        private val newPriceOrderBook: DeltaExchangeL2OrderBookResponse
    ) : DiffUtil.Callback() {
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return this.newPriceOrderBook.buy == this.oldPriceOrderBook.buy && this.newPriceOrderBook.sell == this.oldPriceOrderBook.sell
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return this.newPriceOrderBook.timestamp == this.oldPriceOrderBook.timestamp
        }

        override fun getNewListSize() = this.newPriceOrderBook.buy!!.size

        override fun getOldListSize() = this.oldPriceOrderBook.buy!!.size
    }
}
