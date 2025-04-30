package com.praktikerja.artoz.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.praktikerja.artoz.R

data class Option(val icon: Int, val label: String)

class OptionAdapter(
    private val options: List<Option>,
    private val onOptionSelected: (Option) -> Unit
) : RecyclerView.Adapter<OptionAdapter.OptionViewHolder>() {

    private var selectedPosition = -1 // Track selected item

    inner class OptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ibIcon: ImageButton = itemView.findViewById(R.id.ibIcon)
        val tvLabel: TextView = itemView.findViewById(R.id.tvLabel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_option, parent, false)
        return OptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        val option = options[position]

        // Set icon and label
        holder.ibIcon.setImageResource(option.icon)
        holder.tvLabel.text = option.label

        // Change background color based on selection
        if (position == selectedPosition) {
            holder.ibIcon.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.selectItem)
            )
        } else {
            holder.ibIcon.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, android.R.color.transparent)
            )
        }

        // Handle click
        holder.ibIcon.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousPosition) // Update previous selection
            notifyItemChanged(selectedPosition) // Update new selection
            onOptionSelected(option)
        }
    }

    override fun getItemCount(): Int = options.size
}
