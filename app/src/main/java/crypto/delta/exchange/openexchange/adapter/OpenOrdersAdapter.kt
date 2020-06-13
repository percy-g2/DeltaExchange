package crypto.delta.exchange.openexchange.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import crypto.delta.exchange.openexchange.R
import crypto.delta.exchange.openexchange.pojo.order.CreateOrderResponse

class OpenOrdersAdapter (private var ordersResponseList: List<CreateOrderResponse>) : RecyclerView.Adapter<OpenOrdersAdapter.ViewHolder>() {

    override fun getItemCount() = ordersResponseList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_open_orders, parent, false)
        return ViewHolder(v)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal val contract: AppCompatTextView = itemView.findViewById(R.id.contract)
        internal val quantity: AppCompatTextView = itemView.findViewById(R.id.quantity)
        internal val filledLeft: AppCompatTextView = itemView.findViewById(R.id.filledLeft)
        internal val value: AppCompatTextView = itemView.findViewById(R.id.value)
        internal val type: AppCompatTextView = itemView.findViewById(R.id.type)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ordersResponse = ordersResponseList[position]
        holder.contract.text = ordersResponse.product!!.symbol
        holder.quantity.text = ordersResponse.size!!.toString()
        holder.filledLeft.text = (ordersResponse.size!!.minus(ordersResponse.unfilledSize!!)).toString() + "/" + ordersResponse.unfilledSize!!.toString()
        holder.value.text = ""
        holder.type.text = ordersResponse.orderType
    }
}
