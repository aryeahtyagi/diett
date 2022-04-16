package com.atriasoft.dietplanner.shopping

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.atriasoft.dietplanner.DietPlannerApplication
import com.atriasoft.dietplanner.databinding.FragmentShoppingAddItemBinding
import com.atriasoft.dietplanner.viewmodel.ShoppingViewModel
import com.atriasoft.dietplanner.viewmodel.ShoppingViewModelFactory

class ShoppingAddItemFragment : Fragment() {
    private var _binding: FragmentShoppingAddItemBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ShoppingViewModel by activityViewModels{
        ShoppingViewModelFactory(
            (activity?.application as DietPlannerApplication).database.shoppingDao(),
            (activity?.application as DietPlannerApplication).database.ingredientDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentShoppingAddItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.addItemConfirmBtn.setOnClickListener {
            addNewShoppingItem()
        }

        setupIngredientField(view)
        setupCategoryField(view)
        setupUnitField(view)


    }

    private fun setupIngredientField(view: View) {
        // Setup ingredients inputs autocomplete values
        viewModel.retrieveIngredients().observe(this.viewLifecycleOwner) {
            val ingredientsListNames: MutableList<String>  = mutableListOf()
            for (i in it) {
                ingredientsListNames.add(i.ingredientName)
            }
            val ingredientAutoCompleteAdapter = ArrayAdapter(view.context, R.layout.simple_list_item_1, ingredientsListNames)
            binding.ingredientNameTextInput.setAdapter(ingredientAutoCompleteAdapter)
        }

        binding.ingredientNameTextInput.setOnItemClickListener {_, _, _, _ ->
            viewModel.retrieveCategoryFromIngredientName(binding.ingredientNameTextInput.text.toString())
                .observe(this.viewLifecycleOwner) {
                    binding.categoryTextInput.setText(it[0].categoryName)
                }

        }
    }

    /**
     * Create and set the adapter for categories auto complete from existing categories
     */
    private fun setupCategoryField(view: View) {
        // Setup categories inputs autocomplete values
        viewModel.retrieveIngredientCategories().observe(this.viewLifecycleOwner) {
            val categoriesNameList: MutableList<String> = mutableListOf()
            for (i in it){
                categoriesNameList.add(i.categoryName)
            }
            val categoriesAutoCompleteAdapter = ArrayAdapter(view.context, R.layout.simple_list_item_1, categoriesNameList)
            binding.categoryTextInput.setAdapter(categoriesAutoCompleteAdapter)
        }
    }

    /**
     * Create and sets the adapter for units field
     */
    private fun setupUnitField(view: View) {
        val units = resources.getStringArray(com.atriasoft.dietplanner.R.array.units)
        val unitsAdapter = ArrayAdapter(view.context, R.layout.simple_list_item_1, units)
        binding.amountTypeExposedDropdown.setAdapter(unitsAdapter)
    }

    private fun addNewShoppingItem(){

        if(!validateInputFields())
            return

        val quantityString: String = binding.quantityTextInput.text.toString() +
                " " +
                binding.amountTypeExposedDropdown.text.toString()

        viewModel.addNewShoppingItem(
            binding.categoryTextInput.text.toString(),
            binding.ingredientNameTextInput.text.toString(),
            quantityString
        )
        val action = ShoppingAddItemFragmentDirections.actionShoppingAddItemFragmentToShoppingListFragment()
        findNavController().navigate(action)
    }

    /**
     * Checks the content of TextFields, sets error message if any field is invalid
     */
    private fun validateInputFields() : Boolean {

        var checkValid : Boolean = true
        if (binding.ingredientNameTextInput.text.toString().isEmpty()) {
            binding.ingredientNameTextInput.error = "Enter name of ingredient"
            checkValid = false
        }

        if (binding.categoryTextInput.text.toString().isEmpty()) {
            binding.categoryTextInput.error = "Enter category of ingredient"
            checkValid = false
        }

        if (binding.quantityTextInput.text.toString().isEmpty()) {
            binding.quantityTextInput.error = "Enter the amount"
            checkValid = false
        }

        if(binding.amountTypeExposedDropdown.text.toString().isEmpty()) {
            binding.amountTypeExposedDropdown.error = "Select a unit"
        }

        return checkValid

    }

}