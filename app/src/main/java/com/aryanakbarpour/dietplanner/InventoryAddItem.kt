package com.aryanakbarpour.dietplanner

import android.R
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.aryanakbarpour.dietplanner.databinding.FragmentInventoryAddItemBinding
import com.aryanakbarpour.dietplanner.viewmodel.InventoryViewModel
import com.aryanakbarpour.dietplanner.viewmodel.InventoryViewModelFactory
import java.util.*

class InventoryAddItem : Fragment() {
    private val viewModel: InventoryViewModel by activityViewModels{
        InventoryViewModelFactory(
            (activity?.application as DietPlannerApplication).database.ingredientDao(),
            (activity?.application as DietPlannerApplication).database.inventoryDao()
        )
    }

    private var _binding: FragmentInventoryAddItemBinding? = null
    private val binding get() = _binding!!

    private lateinit var expiryDate: Date

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentInventoryAddItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addItemConfirmBtn.setOnClickListener {addNewInventoryItem()}

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

        // Setup categories inputs autocomplete values
        viewModel.retrieveIngredientCategories().observe(this.viewLifecycleOwner) {
            val categoriesNameList: MutableList<String> = mutableListOf()
            for (i in it){
                categoriesNameList.add(i.categoryName)
            }
            val categoriesAutoCompleteAdapter = ArrayAdapter(view.context, R.layout.simple_list_item_1, categoriesNameList)
            binding.categoryTextInput.setAdapter(categoriesAutoCompleteAdapter)
        }

        // setup date picker
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        binding.expiryPickerInput.setOnFocusChangeListener { _, b ->
            if (b) {
                val datePickerDialog = DatePickerDialog(
                    view.context,
                    DatePickerDialog.OnDateSetListener { _, mYear, mMonth, mDay ->
                        val pickedString = "$mDay/$mMonth/$mYear"
                        binding.expiryPickerInput.setText(pickedString)

                        cal.set(mYear, mMonth, mDay)
                        expiryDate = cal.time
                        println(expiryDate.toString())
                        binding.expiryPickerField.isErrorEnabled = false
                    },
                    year,
                    month,
                    day
                )
                datePickerDialog.show()
            }


        }

        // Setup units
        val units = resources.getStringArray(com.aryanakbarpour.dietplanner.R.array.units)
        val unitsAdapter = ArrayAdapter(view.context, R.layout.simple_list_item_1, units)
        binding.amountTypeExposedDropdown.setAdapter(unitsAdapter)

    }

    private fun addNewInventoryItem() {

        if (!validateInputFields())
            return

        val frozenState = binding.isFrozenSwitch.isChecked

        val quantity = binding.quantityTextInput.text.toString() +
                        " " +
                        binding.amountTypeExposedDropdown.text.toString()


        val name = binding.ingredientNameTextInput.text.toString()
        val category = binding.categoryTextInput.text.toString()

        if (!this::expiryDate.isInitialized && frozenState) {
            if (frozenState)
                expiryDate = Calendar.getInstance().time
            else
                return
        }

        viewModel.addNewInventoryItem(quantity , expiryDate, frozenState, name, category)

        val action = InventoryAddItemDirections.actionInventoryAddItemToInventoryFragment()
        findNavController().navigate(action)
    }

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

        if (!this::expiryDate.isInitialized && !binding.isFrozenSwitch.isChecked) {
            binding.expiryPickerInput.error = "Enter expiry date"
            checkValid = false
        }

        return checkValid

    }
}