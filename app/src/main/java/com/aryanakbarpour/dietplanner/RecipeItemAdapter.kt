package com.aryanakbarpour.dietplanner

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aryanakbarpour.dietplanner.data.Recipe
import com.aryanakbarpour.dietplanner.databinding.RecipeItemBinding

class RecipeItemAdapter() : ListAdapter<Recipe, RecipeItemAdapter.ItemViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecipeItemAdapter.ItemViewHolder {
        return ItemViewHolder(
            RecipeItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: RecipeItemAdapter.ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class ItemViewHolder(private var binding: RecipeItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Recipe) {
            binding.recipeThumbnail.setImageURI(Uri.parse(item.recipe.image))
            binding.titleText.text = item.recipe.title
            binding.cuisineText.text = item.cuisine.name
            binding.caloriesText.text = item.recipe.calories.toString()
            binding.dietText.text = item.diet.name
            binding.preptimeText.text = item.recipe.prepTime.toString()
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