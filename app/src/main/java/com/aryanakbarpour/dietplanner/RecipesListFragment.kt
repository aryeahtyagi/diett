package com.aryanakbarpour.dietplanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aryanakbarpour.dietplanner.databinding.FragmentRecipesListBinding


class RecipesListFragment : Fragment() {

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

        // setup fab button
        binding.floatingActionButton.setOnClickListener {
            val action = RecipesListFragmentDirections.actionRecipesListFragmentToCreateRecipeFragment()
            this.findNavController().navigate(action)
        }
    }

}