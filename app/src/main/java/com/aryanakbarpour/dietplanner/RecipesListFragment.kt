package com.aryanakbarpour.dietplanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.aryanakbarpour.dietplanner.databinding.FragmentRecipesListBinding
import com.aryanakbarpour.dietplanner.viewmodel.RecipeViewModel
import com.aryanakbarpour.dietplanner.viewmodel.RecipeViewModelFactory


class RecipesListFragment : Fragment() {

    private val viewModel: RecipeViewModel by activityViewModels{
        RecipeViewModelFactory(
            (activity?.application as DietPlannerApplication).database.recipeDao(),
            (activity?.application as DietPlannerApplication).database.ingredientDao(),
            (activity?.application as DietPlannerApplication).database.inventoryDao()
        )
    }

    private var _binding: FragmentRecipesListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRecipesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RecipeItemAdapter{
            val action = RecipesListFragmentDirections.actionRecipesListFragmentToRecipeDetailFragment(it.recipe.id)
            this.findNavController().navigate(action)
        }


        binding.recyclerView.adapter = adapter

        viewModel.retrieveAllRecipes().observe(this.viewLifecycleOwner) {items ->
            adapter.submitList(items)
        }

        binding.recyclerView.layoutManager = GridLayoutManager(this.context,1)
        // setup fab button
        binding.floatingActionButton.setOnClickListener {
            val action = RecipesListFragmentDirections.actionRecipesListFragmentToCreateRecipeFragment()
            this.findNavController().navigate(action)
        }
    }

}