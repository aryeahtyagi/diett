package com.aryanakbarpour.dietplanner

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aryanakbarpour.dietplanner.data.Recipe
import com.aryanakbarpour.dietplanner.databinding.RecipeItemBinding

class RecipeItemAdapter(private val onItemClicked: (Recipe) -> Unit) : ListAdapter<Recipe, RecipeItemAdapter.ItemViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        return ItemViewHolder(
            RecipeItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: RecipeItemAdapter.ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener { onItemClicked(current) }
        holder.bind(current)
    }

    class ItemViewHolder(private var binding: RecipeItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Recipe) {
            binding.apply {
                if (item.recipe.image != null)
                    recipeThumbnail.setImageURI(Uri.parse(item.recipe.image))
                titleText.text = item.recipe.title
                cuisineText.text = item.cuisine.name
                caloriesText.text = item.recipe.calories.toString()
                dietText.text = item.diet.name
                preptimeText.text = item.recipe.prepTime.toString()
            }

        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Recipe>() {
            override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
                return oldItem.recipe.id == newItem.recipe.id
            }
        }
    }


}