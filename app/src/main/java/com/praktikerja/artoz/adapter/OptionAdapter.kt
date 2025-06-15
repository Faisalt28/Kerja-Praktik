package com.praktikerja.artoz.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.praktikerja.artoz.R

data class Option(val icon: Int, val label: String)

class OptionAdapter(
    private val options: List<Option>,
    private val onOptionSelected: (Option) -> Unit,
    private val initialSelectedLabel: String? = null
) : RecyclerView.Adapter<OptionAdapter.OptionViewHolder>() {

    private var selectedPosition = -1

    init {
        initialSelectedLabel?.let {
            selectedPosition = options.indexOfFirst { opt -> opt.label == it }
        }
    }

    inner class OptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)
        val tvLabel: TextView = itemView.findViewById(R.id.tvLabel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_option, parent, false)
        return OptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        val option = options[position]

        holder.ivIcon.setImageResource(option.icon)
        holder.tvLabel.text = option.label

        // Update background
        val colorRes = if (position == selectedPosition) R.color.selectItem else android.R.color.transparent
        holder.itemView.setBackgroundColor(
            ContextCompat.getColor(holder.itemView.context, colorRes)
        )

        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            onOptionSelected(option)
        }
    }

    override fun getItemCount(): Int = options.size
}
