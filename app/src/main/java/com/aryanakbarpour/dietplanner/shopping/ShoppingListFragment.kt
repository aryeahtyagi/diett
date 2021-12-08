package com.aryanakbarpour.dietplanner.shopping

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.aryanakbarpour.dietplanner.DietPlannerApplication
import com.aryanakbarpour.dietplanner.R
import com.aryanakbarpour.dietplanner.SwipeGesture
import com.aryanakbarpour.dietplanner.data.ShoppingItem
import com.aryanakbarpour.dietplanner.databinding.FragmentShoppingListBinding
import com.aryanakbarpour.dietplanner.viewmodel.ShoppingViewModel
import com.aryanakbarpour.dietplanner.viewmodel.ShoppingViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import java.util.*


class ShoppingListFragment : Fragment() {

    private var _binding: FragmentShoppingListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ShoppingViewModel by activityViewModels{
        ShoppingViewModelFactory(
            (activity?.application as DietPlannerApplication).database.shoppingDao(),
            (activity?.application as DietPlannerApplication).database.ingredientDao()
        )
    }

    private lateinit var expiryDate: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.shopping_options_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.opt_clear -> showClearConfirmationDialog()
            R.id.opt_transfer -> moveMarkedToInventory(this.requireContext())
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentShoppingListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setup recycler view
        val adapter = ShoppingItemAdapter()


        binding.recyclerView.layoutManager = GridLayoutManager(this.context,1)
        binding.recyclerView.adapter = adapter

        viewModel.retrieveAllShoppingItems().observe(this.viewLifecycleOwner) { items ->

            adapter.submitList(items)
            val swipeGesture = object : SwipeGesture(view.context) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    super.onSwiped(viewHolder, direction)
                    when(direction){
                        ItemTouchHelper.LEFT -> {
                            //Delete Item
                            viewModel.deleteShoppingItem(items[viewHolder.adapterPosition].shoppingItemDetail)
                            // shoppingItemList.removeAt(viewHolder.adapterPosition)
                        }
                        ItemTouchHelper.RIGHT -> {
                            //Check item
                            val oldItem = items[viewHolder.adapterPosition]
                            viewModel.setCheckShoppingItem(oldItem.shoppingItemDetail, true)
                            //adapter.notifyItemRemoved(viewHolder.absoluteAdapterPosition)
                        }
                    }

                }
            }

            val itemTouchHelper = ItemTouchHelper(swipeGesture)
            itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        }

        // setup fab button
        binding.floatingActionButton.setOnClickListener {
            val action = ShoppingListFragmentDirections.actionShoppingListFragmentToShoppingAddItemFragment()
            this.findNavController().navigate(action)
        }
    }

    fun moveMarkedToInventory(context: Context) {
        viewModel.retrieveMarkedItems().observe(this.viewLifecycleOwner) { items ->
            for (i  in items) {
                showMoveToInventoryDialog(context, i)
            }

        }
    }


    private fun showClearConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage("Are you sure you want to clear the shopping list!")
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.retrieveAllShoppingItems().observe(this.viewLifecycleOwner) { items ->
                    for (i  in items){
                        viewModel.deleteShoppingItem(i.shoppingItemDetail)
                    }
                }
            }
            .show()
    }

    private fun showMoveToInventoryDialog(context: Context, shoppingItem: ShoppingItem) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.fragment_inventory_item_detail)

        val categoryTextInput = dialog.findViewById<TextInputEditText>(R.id.category_text_input)
        val ingredientTextInput = dialog.findViewById<TextInputEditText>(R.id.ingredient_text_input)

        categoryTextInput.setText(shoppingItem.ingredient.category.categoryName)
        ingredientTextInput.setText(shoppingItem.ingredient.ingredient.ingredientName)

        val amountTextInput = dialog.findViewById<TextInputEditText>(R.id.quantity_text_input)
        val unitTextInput = dialog.findViewById<MaterialAutoCompleteTextView>(R.id.amount_type_exposed_dropdown)

        amountTextInput.setText(shoppingItem.shoppingItemDetail.quantity.split(' ')[0])
        unitTextInput.setText(shoppingItem.shoppingItemDetail.quantity.split(' ')[1])

        val expiryInput = dialog.findViewById<TextInputEditText>(R.id.expiry_picker_input)
        val frozenSwitchInput = dialog.findViewById<SwitchMaterial>(R.id.is_frozen_switch)

        setupUnitField(context, unitTextInput)
        setupExpiryPickerField(context, expiryInput)

        val confirmBtn = dialog.findViewById<Button>(R.id.update_btn)
        confirmBtn.setText("Add to Inventory")
        confirmBtn.setOnClickListener{
            val quantityString = "${amountTextInput.text.toString()} ${unitTextInput.text.toString()}"
            viewModel.addShoppingItemToInventory(ingredientId= shoppingItem.ingredient.ingredient.id,
                quantity= quantityString,
                expiryDate= expiryDate,
                frozen= frozenSwitchInput.isChecked)
            viewModel.deleteShoppingItem(shoppingItem.shoppingItemDetail)
            dialog.dismiss()
        }

        val deleteBtn = dialog.findViewById<Button>(R.id.delete_btn)
        deleteBtn.setOnClickListener {
            viewModel.deleteShoppingItem(shoppingItem.shoppingItemDetail)
            dialog.dismiss()
        }

        dialog.show()
    }

    /**
     * Create and sets the adapter for units field
     */
    private fun setupUnitField(context: Context, field: MaterialAutoCompleteTextView) {
        val units = resources.getStringArray(R.array.units)
        val unitsAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, units)
        field.setAdapter(unitsAdapter)
    }

    /**
     * Binds a ExpiryPickerDialog to expiry picker
     */
    private fun setupExpiryPickerField(context: Context, expiryInput: TextInputEditText ) {
        // setup date picker
        val cal = Calendar.getInstance()


        expiryInput.setOnFocusChangeListener { _, b ->
            if (b) {
                val datePickerDialog = DatePickerDialog(
                    context,
                    DatePickerDialog.OnDateSetListener { _, mYear, mMonth, mDay ->
                        val pickedString = "$mDay/$mMonth/$mYear"
                        expiryInput.setText(pickedString)

                        cal.set(mYear, mMonth, mDay)
                        expiryDate = cal.time
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.show()
            }
        }
    }


}