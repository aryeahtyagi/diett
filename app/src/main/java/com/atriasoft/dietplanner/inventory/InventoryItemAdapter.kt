package com.atriasoft.dietplanner.inventory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atriasoft.dietplanner.data.InventoryItem
import com.atriasoft.dietplanner.databinding.InventoryItemBinding
import java.text.SimpleDateFormat
import java.util.*

class InventoryItemAdapter(private val onItemClicked: (InventoryItem) -> Unit) : ListAdapter<InventoryItem, InventoryItemAdapter.ItemViewHolder>(
    DiffCallback
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        return ItemViewHolder(
            InventoryItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener { onItemClicked(current) }
        holder.bind(current)
    }

    class ItemViewHolder(private var binding: InventoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: InventoryItem) {

            binding.apply {
                ingredientName.text = item.ingredient.ingredientName
                quantity.text = item.inventoryItemDetail.quantity
                if (item.inventoryItemDetail.isFrozen) {
                    expiry.text = ""
                } else {
                    expiry.text= item.inventoryItemDetail.expiry.dateToString("dd/MM/yyyy")
                    expiry.setCompoundDrawables(null, null, null, null)
                }
            }
        }
        private fun Date.dateToString(format: String): String {
            val dateFormatter = SimpleDateFormat(format, Locale.getDefault())
            return dateFormatter.format(this)
        }
    }



    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<InventoryItem>() {
            override fun areItemsTheSame(oldItem: InventoryItem, newItem: InventoryItem): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: InventoryItem, newItem: InventoryItem): Boolean {
                return oldItem.inventoryItemDetail.id == newItem.inventoryItemDetail.id
            }
        }
    }
}