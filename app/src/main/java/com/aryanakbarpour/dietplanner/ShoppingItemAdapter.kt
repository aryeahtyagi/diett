package com.aryanakbarpour.dietplanner

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aryanakbarpour.dietplanner.data.ShoppingItem
import com.aryanakbarpour.dietplanner.databinding.ShoppingItemBinding

class ShoppingItemAdapter() : ListAdapter<ShoppingItem, ShoppingItemAdapter.ItemViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShoppingItemAdapter.ItemViewHolder {
        return ItemViewHolder(
            ShoppingItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }


    override fun onBindViewHolder(holder: ShoppingItemAdapter.ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class ItemViewHolder(private var binding: ShoppingItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ShoppingItem) {

            binding.apply {
                ingredientName.text = item.ingredient.ingredient.ingredientName
                quantity.text = item.shoppingItemDetail.quantity

                if (item.shoppingItemDetail.checked)
                    binding.ingredientName.paintFlags = binding.ingredientName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ShoppingItem>() {
            override fun areItemsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean {
                return oldItem.shoppingItemDetail.id == newItem.shoppingItemDetail.id
            }
        }
    }


}