package com.atriasoft.dietplanner.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.atriasoft.dietplanner.DietPlannerApplication
import com.atriasoft.dietplanner.databinding.FragmentInventoryBinding
import com.atriasoft.dietplanner.viewmodel.InventoryViewModel
import com.atriasoft.dietplanner.viewmodel.InventoryViewModelFactory

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
        binding.recyclerView.layoutManager = GridLayoutManager(this.context,1)
        binding.recyclerView.adapter = adapter
        viewModel.retrieveAllInventoryItems().observe(this.viewLifecycleOwner) {
            adapter.submitList(it)
        }



        binding.floatingActionButton.setOnClickListener {
            val action = InventoryFragmentDirections.actionInventoryFragmentToInventoryAddItem()
            this.findNavController().navigate(action)
        }
    }

}