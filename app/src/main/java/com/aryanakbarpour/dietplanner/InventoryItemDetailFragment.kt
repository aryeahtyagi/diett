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
import androidx.navigation.fragment.navArgs
import com.aryanakbarpour.dietplanner.data.InventoryItemDetail
import com.aryanakbarpour.dietplanner.databinding.FragmentInventoryItemDetailBinding
import com.aryanakbarpour.dietplanner.viewmodel.InventoryViewModel
import com.aryanakbarpour.dietplanner.viewmodel.InventoryViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*
import com.aryanakbarpour.dietplanner.R as PR

class InventoryItemDetailFragment : Fragment() {
    lateinit var itemDetail: InventoryItemDetail

    private val viewModel: InventoryViewModel by activityViewModels {
        InventoryViewModelFactory(
            (activity?.application as DietPlannerApplication).database.ingredientDao(),
            (activity?.application as DietPlannerApplication).database.inventoryDao()
        )
    }

    private val navigationArgs: InventoryItemDetailFragmentArgs by navArgs()
    private lateinit var expiryDate: Date

    private var _binding: FragmentInventoryItemDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentInventoryItemDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navigationArgs.itemId
        viewModel.retrieveItemDetailById(id).observe(this.viewLifecycleOwner) { selectedItem ->
            itemDetail = selectedItem

            binding.updateBtn.setOnClickListener {
                updateInventoryItem(itemDetail)
            }

            binding.apply {
                val quantitySeg = itemDetail.quantity.split("\\s".toRegex())
                quantityTextInput.setText(quantitySeg[0])
                amountTypeExposedDropdown.setText(quantitySeg[quantitySeg.size-1])
                expiryPickerInput.setText(itemDetail.expiry.dateToString("dd/MM/yyyy"))
                isFrozenSwitch.isChecked = itemDetail.isFrozen
            }

            setupUnitField(view)
            viewModel.retrieveIngredientById(itemDetail.ingredientId).observe(this.viewLifecycleOwner) { ingredient ->
                binding.ingredientTextInput.setText(ingredient.ingredientName)

                viewModel.retrieveCategoryFromIngredientName(ingredient.ingredientName).observe(this.viewLifecycleOwner) { category ->
                    binding.categoryTextInput.setText(category[0].categoryName)
                }
            }


        }

        binding.deleteBtn.setOnClickListener {
            showConfirmationDialog()
        }

        setupExpiryPickerField(view)


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
    }

    /**
     * Create and sets the adapter for units field
     */
    private fun setupUnitField(view: View) {
        val units = resources.getStringArray(com.aryanakbarpour.dietplanner.R.array.units)
        val unitsAdapter = ArrayAdapter(view.context, R.layout.simple_list_item_1, units)
        binding.amountTypeExposedDropdown.setAdapter(unitsAdapter)
    }

    private fun updateInventoryItem(itemDetail: InventoryItemDetail) {

        if (binding.quantityTextInput.text.toString().isEmpty()) {
            binding.quantityTextInput.error = "can not be empty"
            return
        }


        val frozenState = binding.isFrozenSwitch.isChecked

        val quantity = binding.quantityTextInput.text.toString() +
                " " +
                binding.amountTypeExposedDropdown.text.toString()


        val date = if (this::expiryDate.isInitialized) {
            expiryDate
        }
        else {
            itemDetail.expiry
        }

        val i = InventoryItemDetail(id = itemDetail.id,
            ingredientId = itemDetail.ingredientId,
            quantity = quantity,
            expiry = date,
            isFrozen = frozenState)

        viewModel.updateInventoryItemDetail(i)

        val action = InventoryItemDetailFragmentDirections.actionInventoryIteamDetailFragmentToInventoryFragment()
        findNavController().navigate(action)

    }

    private fun deleteInventoryItem() {
        viewModel.deleteInventoryItemById(navigationArgs.itemId)
        val action = InventoryItemDetailFragmentDirections.actionInventoryIteamDetailFragmentToInventoryFragment()
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun Date.dateToString(format: String): String {
        val dateFormatter = SimpleDateFormat(format, Locale.getDefault())
        return dateFormatter.format(this)
    }

    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(PR.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(PR.string.no)) { _, _ -> }
            .setPositiveButton(getString(PR.string.yes)) { _, _ ->
                deleteInventoryItem()
            }
            .show()
    }

}