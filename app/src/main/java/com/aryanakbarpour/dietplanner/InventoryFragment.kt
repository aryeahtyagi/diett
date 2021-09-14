package com.aryanakbarpour.dietplanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aryanakbarpour.dietplanner.data.InventoryItem
import com.aryanakbarpour.dietplanner.databinding.FragmentInventoryBinding
import com.aryanakbarpour.dietplanner.viewmodel.InventoryViewModel
import com.aryanakbarpour.dietplanner.viewmodel.InventoryViewModelFactory

class InventoryFragment : Fragment() {

    private var _binding: FragmentInventoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InventoryViewModel by activityViewModels{
        InventoryViewModelFactory(
            (activity?.application as DietPlannerApplication).database.ingredientDao(),
            (activity?.application as DietPlannerApplication).database.inventoryDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = InventoryItemAdapter {
            val action = InventoryFragmentDirections.actionInventoryFragmentToInventoryItemDetailFragment(it.inventoryItemDetail.id)
            this.findNavController().navigate(action)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = adapter
        viewModel.retrieveIngredientInventoryItems().observe(this.viewLifecycleOwner) { items ->
            val inventoryItemsList : MutableList<InventoryItem> = mutableListOf()
            for (i in items) {
                for (detail in i.inventoryItemDetail){
                    inventoryItemsList.add(InventoryItem(detail, i.ingredient.ingredientName, ""))
                }
            }
            inventoryItemsList.let {
                println("submitting list of size : ${it.size}")
                adapter.submitList(it)
            }
        }



        binding.floatingActionButton.setOnClickListener {
            val action = InventoryFragmentDirections.actionInventoryFragmentToInventoryAddItem()
            this.findNavController().navigate(action)
        }
    }

}