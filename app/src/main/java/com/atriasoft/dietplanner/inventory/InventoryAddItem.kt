package com.atriasoft.dietplanner.inventory

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
import com.atriasoft.dietplanner.DietPlannerApplication
import com.atriasoft.dietplanner.databinding.FragmentInventoryAddItemBinding
import com.atriasoft.dietplanner.viewmodel.InventoryViewModel
import com.atriasoft.dietplanner.viewmodel.InventoryViewModelFactory
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

        setupIngredientField(view)
        setupCategoryField(view)
        setupExpiryPickerField(view)
        setupUnitField(view)

    }

    /**
     * Create and sets the adapter for ingredients auto complete from existing ingredients
     */
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
     * Binds a ExpiryPickerDialog to expiry picker
     */
    private fun setupExpiryPickerField(view: View) {
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
                        binding.expiryPickerField.isErrorEnabled = false

                    },
                    year,
                    month,
                    day
                )
                datePickerDialog.show()
            }
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

    /**
     * Extracts the contents of fields and process them. Calls view model to insert an item.
     */
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

        if (!this::expiryDate.isInitialized && !binding.isFrozenSwitch.isChecked) {
            binding.expiryPickerInput.error = "Enter expiry date"
            checkValid = false
        }

        return checkValid

    }
}