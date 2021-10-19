package com.aryanakbarpour.dietplanner

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.aryanakbarpour.dietplanner.databinding.FragmentCreateRecipeBinding
import com.aryanakbarpour.dietplanner.viewmodel.RecipeIngredientModel
import com.aryanakbarpour.dietplanner.viewmodel.RecipeViewModel
import com.aryanakbarpour.dietplanner.viewmodel.RecipeViewModelFactory
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText

class CreateRecipeFragment : Fragment() {

    private val viewModel: RecipeViewModel by activityViewModels{
        RecipeViewModelFactory(
            (activity?.application as DietPlannerApplication).database.recipeDao(),
            (activity?.application as DietPlannerApplication).database.ingredientDao(),
            (activity?.application as DietPlannerApplication).database.inventoryDao()
        )
    }

    private var _binding: FragmentCreateRecipeBinding? = null
    private val binding get() = _binding!!

    var imagePath: Uri?=null
    val recipeIngredientsList: MutableList<RecipeIngredientModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCreateRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Image picker
        binding.coverImage.setOnClickListener {
            ImagePicker.with(this)
                .crop(1f, 1f)
                .maxResultSize(500, 500)
                .createIntent {
                    startForCoverImageResult.launch(it)
                }
        }

        // Add ingredient dialog
        binding.addIngredientButton.setOnClickListener {
            showAddIngredientDialog(view.context)
        }

        setupDietField(view.context)
        setupCuisineField(view.context)
        setupFoodTypeField(view.context)

        binding.confirmButton.setOnClickListener {
            val dietName = binding.dietTextInput.text.toString()
            val foodTypeName = binding.typeTextInput.text.toString()
            val cuisineName = binding.cuisineTextInput.text.toString()
            val title = binding.titleTextInput.text.toString()
            val servings = binding.servingsTextInput.text.toString()
            val prepTime = binding.preptimeTextInput.text.toString().toInt()
            val calories = binding.calsTextInput.text.toString().toDouble()
            val instruction = binding.instructionTextInput.text.toString()

            println("yoooooooohooooooooooo, $imagePath")
            viewModel.insertRecipe(
                dietName, foodTypeName, cuisineName, recipeIngredientsList,
                title, servings, imagePath, instruction, prepTime, calories)

            val action = CreateRecipeFragmentDirections.actionCreateRecipeFragmentToRecipesListFragment()
            this.findNavController().navigate(action)
        }

    }

    private val startForCoverImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                val fileUri = data?.data!!

                imagePath = fileUri
                binding.coverImage.setImageURI(fileUri)
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this.context, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this.context, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    private fun showAddIngredientDialog(context: Context) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.recipe_add_fragment_dialog)

        val categoryTextInput = dialog.findViewById<MaterialAutoCompleteTextView>(R.id.category_text_input)
        val ingredientTextInput = dialog.findViewById<MaterialAutoCompleteTextView>(R.id.ingredient_name_text_input)
        val amountTextInput = dialog.findViewById<TextInputEditText>(R.id.quantity_text_input)
        val unitTextInput = dialog.findViewById<MaterialAutoCompleteTextView>(R.id.amount_type_exposed_dropdown)

        setupIngredientField(context, ingredientTextInput, categoryTextInput)
        setupCategoryField(context, categoryTextInput)
        setupUnitField(context, unitTextInput)

        dialog.findViewById<Button>(R.id.add_item_confirm_btn).setOnClickListener{
            val newIngredientItem = layoutInflater.inflate(R.layout.shopping_item, null)
            newIngredientItem.id = recipeIngredientsList.size

            val listItemIngredientName = newIngredientItem.findViewById<TextView>(R.id.ingredient_name)
            val listItemAmount = newIngredientItem.findViewById<TextView>(R.id.quantity)
            val listItemDeleteIcon = newIngredientItem.findViewById<ImageView>(R.id.delete_icon)

            val amountText = "${amountTextInput.text.toString()} ${unitTextInput.text.toString()}"

            listItemIngredientName.text = ingredientTextInput.text.toString()
            listItemAmount.text = amountText

            listItemDeleteIcon.visibility = View.VISIBLE
            listItemDeleteIcon.setOnClickListener {
                recipeIngredientsList[newIngredientItem.id].deleted = true
                binding.ingredientsList.removeView(newIngredientItem)
            }

            recipeIngredientsList.add(viewModel.createRecipeIngredientMode(
                categoryTextInput.text.toString(),
                ingredientTextInput.text.toString(),
                amountText))

            binding.ingredientsList.addView(newIngredientItem)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setupDietField(context: Context) {
        viewModel.retrieveDiets().observe(this.viewLifecycleOwner) { dietList ->
            // TODO: Change other field setups similar to this
            val dietNamesList = dietList.map { it.name }
            val dietsAutoCompleteAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, dietNamesList)
            binding.dietTextInput.setAdapter(dietsAutoCompleteAdapter)
        }
    }

    private fun setupFoodTypeField(context: Context) {
        viewModel.retrieveFoodTypes().observe(this.viewLifecycleOwner) { foodTypeList ->
            val foodTypeNamesList = foodTypeList.map { it.name }
            val autoCompleteAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, foodTypeNamesList)
            binding.typeTextInput.setAdapter(autoCompleteAdapter)
        }
    }

    private fun setupCuisineField(context: Context) {
        viewModel.retrieveCuisines().observe(this.viewLifecycleOwner) { cuisineList ->
            val cuisineNamesList = cuisineList.map { it.name }
            val autoCompleteAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, cuisineNamesList)
            binding.cuisineTextInput.setAdapter(autoCompleteAdapter)
        }
    }

    /**
     * Create and sets the adapter for ingredients auto complete from existing ingredients
     */
    private fun setupIngredientField(context: Context, ingredientField: MaterialAutoCompleteTextView, categoryField: MaterialAutoCompleteTextView) {
        // Setup ingredients inputs autocomplete values
        viewModel.retrieveIngredients().observe(this.viewLifecycleOwner) {
            val ingredientsListNames: MutableList<String>  = mutableListOf()
            for (i in it) {
                ingredientsListNames.add(i.ingredientName)
            }
            val ingredientAutoCompleteAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, ingredientsListNames)
            ingredientField.setAdapter(ingredientAutoCompleteAdapter)
        }

        ingredientField.setOnItemClickListener {_, _, _, _ ->
            viewModel.retrieveCategoryFromIngredientName(ingredientField.text.toString())
                .observe(this.viewLifecycleOwner) {
                    categoryField.setText(it[0].categoryName)
                }

        }
    }

    /**
     * Create and set the adapter for categories auto complete from existing categories
     */
    private fun setupCategoryField(context: Context, field: MaterialAutoCompleteTextView) {
        // Setup categories inputs autocomplete values
        viewModel.retrieveIngredientCategories().observe(this.viewLifecycleOwner) {
            val categoriesNameList: MutableList<String> = mutableListOf()
            for (i in it){
                categoriesNameList.add(i.categoryName)
            }
            val categoriesAutoCompleteAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, categoriesNameList)
            field.setAdapter(categoriesAutoCompleteAdapter)
        }
    }

    /**
     * Create and sets the adapter for units field
     */
    private fun setupUnitField(context: Context, field: MaterialAutoCompleteTextView) {
        val units = resources.getStringArray(com.aryanakbarpour.dietplanner.R.array.units)
        val unitsAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, units)
        field.setAdapter(unitsAdapter)
    }

}