package com.example.smartdukaan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemAdapter(
    private val items: List<Item>,
    private val onItemClick: (Item) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvNameUrdu: TextView = view.findViewById(R.id.tvNameUrdu)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        val tvQty: TextView = view.findViewById(R.id.tvQty)
        val tvLowStock: TextView = view.findViewById(R.id.tvLowStock)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.tvName.text = item.name
        holder.tvNameUrdu.text = item.nameUrdu
        holder.tvPrice.text = "Rs ${item.sellingPrice}"
        holder.tvQty.text = item.qty.toInt().toString()

        // Show low stock warning
        if (item.qty <= 10 && item.qty > 0) {
            holder.tvLowStock.visibility = View.VISIBLE
        } else {
            holder.tvLowStock.visibility = View.GONE
        }

        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount() = items.size
}

