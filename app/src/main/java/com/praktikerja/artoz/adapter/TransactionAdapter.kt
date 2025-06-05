package com.praktikerja.artoz.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.praktikerja.artoz.R
import com.praktikerja.artoz.data.TransactionEntity
import com.praktikerja.artoz.utils.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(private val onItemClick: (TransactionEntity) -> Unit) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private var transactions: List<TransactionEntity> = emptyList()

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgIcon: ImageView = itemView.findViewById(R.id.imgIcon)
        val textType: TextView = itemView.findViewById(R.id.textType)
        val textCategory: TextView = itemView.findViewById(R.id.textCategory)
        val textAmount: TextView = itemView.findViewById(R.id.textAmount)
        val textDate: TextView = itemView.findViewById(R.id.textDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]

        holder.imgIcon.setImageResource(transaction.iconResId)
        holder.textType.text = transaction.type
        holder.textCategory.text = transaction.category

        // Format jumlah dengan CurrencyFormatter dan tanda + atau -
        val formattedAmount = CurrencyFormatter.formatRupiah(transaction.amount)
        val amountText = if (transaction.type == "Pemasukan") {
            "+ $formattedAmount"
        } else {
            "- $formattedAmount"
        }
        holder.textAmount.text = amountText

        val colorRes = if (transaction.type == "Pemasukan") {
            R.color.green
        } else {
            R.color.red
        }
        holder.textAmount.setTextColor(holder.itemView.context.getColor(colorRes))

        holder.textDate.text = formatDate(transaction.date)

        holder.itemView.setOnClickListener {
            onItemClick(transaction)
        }
    }

    override fun getItemCount(): Int = transactions.size

    fun submitList(newList: List<TransactionEntity>) {
        transactions = newList
        notifyDataSetChanged()
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
