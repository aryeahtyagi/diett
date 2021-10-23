package com.aryanakbarpour.dietplanner

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.aryanakbarpour.dietplanner.data.Recipe
import com.aryanakbarpour.dietplanner.databinding.FragmentRecipeDetailBinding
import com.aryanakbarpour.dietplanner.viewmodel.RecipeViewModel
import com.aryanakbarpour.dietplanner.viewmodel.RecipeViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class RecipeDetailFragment : Fragment() {

    lateinit var recipe: Recipe

    private val viewModel: RecipeViewModel by activityViewModels {
        RecipeViewModelFactory(
            (activity?.application as DietPlannerApplication).database.recipeDao(),
            (activity?.application as DietPlannerApplication).database.ingredientDao(),
            (activity?.application as DietPlannerApplication).database.inventoryDao()
        )
    }

    private val navigationArgs: RecipeDetailFragmentArgs by navArgs()

    private var _binding: FragmentRecipeDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRecipeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = navigationArgs.recipeId
        viewModel.retrieveRecipeById(id).observe(this.viewLifecycleOwner) { recipeDetail ->
            binding.apply {
                title.text = recipeDetail.recipe.title
                if (recipeDetail.recipe.image != null)
                    recipeThumbnail.setImageURI(Uri.parse(recipeDetail.recipe.image))
                dietText.text = recipeDetail.diet.name
                cuisineText.text = recipeDetail.cuisine.name
                typeText.text = recipeDetail.foodType.name
                caloriesText.text = "${recipeDetail.recipe.calories.toString()} kcal"
                servingsText.text = recipeDetail.recipe.servings.toString()
                preptimeText.text = "${recipeDetail.recipe.prepTime}'"

                instructionText.text = recipeDetail.recipe.instruction

                for (ingredient in recipeDetail.ingredients){
                    val newIngredientItem = layoutInflater.inflate(R.layout.shopping_item, null)
                    newIngredientItem.id = recipeDetail.ingredients.size

                    val listItemIngredientName = newIngredientItem.findViewById<TextView>(R.id.ingredient_name)
                    val listItemAmount = newIngredientItem.findViewById<TextView>(R.id.quantity)

                    listItemIngredientName.text = ingredient.ingredient.ingredientName
                    listItemAmount.text = ingredient.recipeIngredientDetail.amount

                    ingredientsList.addView(newIngredientItem)
                }

                deleteBtn.setOnClickListener {
                    showDeleteConfirmationDialog(recipeDetail)
                }

                reduceInventoryBtn.setOnClickListener {
                    viewModel.inventoryProcessRecipeIngredients(recipeDetail.ingredients)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showDeleteConfirmationDialog(recipe: Recipe) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteRecipe(recipe)
            }
            .show()
    }

    private fun deleteRecipe(recipe: Recipe) {
        viewModel.deleteRecipe(recipe)
        val action = RecipeDetailFragmentDirections.actionRecipeDetailFragmentToRecipesListFragment()
        findNavController().navigate(action)
    }

}