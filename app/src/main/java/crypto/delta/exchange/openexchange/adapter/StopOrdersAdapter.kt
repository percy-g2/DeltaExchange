package crypto.delta.exchange.openexchange.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import crypto.delta.exchange.openexchange.R
import crypto.delta.exchange.openexchange.interfaces.OrdersInterface
import crypto.delta.exchange.openexchange.pojo.order.CreateOrderResponse

class StopOrdersAdapter(
    private var ordersResponseList: List<CreateOrderResponse>,
    private val ordersInterface: OrdersInterface
) : RecyclerView.Adapter<StopOrdersAdapter.ViewHolder>() {

    override fun getItemCount() = ordersResponseList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_stop_orders, parent, false)
        return ViewHolder(v)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal val contract: AppCompatTextView = itemView.findViewById(R.id.contract)
        internal val quantity: AppCompatTextView = itemView.findViewById(R.id.quantity)
        internal val stopPrice: AppCompatTextView = itemView.findViewById(R.id.stopPrice)
        internal val type: AppCompatTextView = itemView.findViewById(R.id.type)
        private val btnCancelOrder: MaterialButton = itemView.findViewById(R.id.btnCancelOrder)

        init {
            btnCancelOrder.setOnClickListener {
                ordersInterface.onCancelOrder(ordersResponseList[adapterPosition])
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ordersResponse = ordersResponseList[position]
        holder.contract.text = ordersResponse.product!!.symbol
        holder.quantity.text = ordersResponse.size!!.toString()
        holder.stopPrice.text = ordersResponse.stopPrice.toString()

        when {
            ordersResponse.orderType.equals("limit_order") -> {
                holder.type.text = "Limit"
            }
            ordersResponse.orderType.equals("market_order") -> {
                holder.type.text = "Market"
            }
            else -> {
                holder.type.text = ordersResponse.orderType
            }
        }

    }
}
