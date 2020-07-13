package crypto.delta.exchange.openexchange.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import crypto.delta.exchange.openexchange.R
import crypto.delta.exchange.openexchange.pojo.position.OpenPositionResponse
import java.lang.String
import java.util.*

class PositionAdapter(private var ordersResponseList: List<OpenPositionResponse>) :
    RecyclerView.Adapter<PositionAdapter.ViewHolder>() {

    override fun getItemCount() = ordersResponseList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_open_position, parent, false)
        return ViewHolder(v)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal val contract: AppCompatTextView = itemView.findViewById(R.id.contract)
        internal val quantity: AppCompatTextView = itemView.findViewById(R.id.quantity)
        internal val entryPrice: AppCompatTextView = itemView.findViewById(R.id.entryPrice)
        internal val liquidationPrice: AppCompatTextView =
            itemView.findViewById(R.id.liquidationPrice)
        internal val margin: AppCompatTextView = itemView.findViewById(R.id.margin)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ordersResponse = ordersResponseList[position]
        holder.contract.text = ordersResponse.product!!.symbol
        holder.quantity.text = ordersResponse.size!!.toString()
        holder.entryPrice.text = String.format(
            Locale.ENGLISH, "%.2f", ordersResponse.entryPrice.toDouble()
        )
        holder.liquidationPrice.text = String.format(
            Locale.ENGLISH, "%.2f", ordersResponse.liquidationPrice.toDouble()
        )
        holder.margin.text = ordersResponse.margin

    }
}
