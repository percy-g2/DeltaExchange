package crypto.delta.exchange.openexchange.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import crypto.delta.exchange.openexchange.R
import crypto.delta.exchange.openexchange.pojo.WalletResponse

class WalletAdapter(private var walletResponseList: List<WalletResponse>) :
    RecyclerView.Adapter<WalletAdapter.ViewHolder>() {

    override fun getItemCount() = walletResponseList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_wallet, parent, false)
        return ViewHolder(v)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val coin: AppCompatTextView = itemView.findViewById(R.id.coin)
        internal val walletBalance: AppCompatTextView = itemView.findViewById(R.id.walletBalance)
        internal val positionMargin: AppCompatTextView = itemView.findViewById(R.id.positionMargin)
        internal val orderMargin: AppCompatTextView = itemView.findViewById(R.id.orderMargin)
        internal val availableBalance: AppCompatTextView =
            itemView.findViewById(R.id.availableBalance)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val wallet = walletResponseList[position]
        holder.coin.text = wallet.asset.symbol
        holder.walletBalance.text = wallet.balance
        holder.positionMargin.text = wallet.positionMargin
        holder.orderMargin.text = wallet.orderMargin
        holder.availableBalance.text = wallet.availableBalance
    }
}
