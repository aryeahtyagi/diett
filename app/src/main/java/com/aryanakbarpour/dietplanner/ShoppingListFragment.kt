package com.aryanakbarpour.dietplanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.aryanakbarpour.dietplanner.data.ShoppingItem
import com.aryanakbarpour.dietplanner.databinding.FragmentShoppingListBinding
import com.aryanakbarpour.dietplanner.viewmodel.ShoppingViewModel
import com.aryanakbarpour.dietplanner.viewmodel.ShoppingViewModelFactory


class ShoppingListFragment : Fragment() {

    private var _binding: FragmentShoppingListBinding? = null
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
        _binding = FragmentShoppingListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setup recycler view
        val adapter = ShoppingItemAdapter()


        binding.recyclerView.layoutManager = GridLayoutManager(this.context,1)
        binding.recyclerView.adapter = adapter

        viewModel.retrieveIngredientShoppingItems().observe(this.viewLifecycleOwner) { items ->

            val shoppingItemList : MutableList<ShoppingItem> = mutableListOf()
            for (i in items) {
                for (detail in i.shoppingItemDetails) {
                    // val cat = viewModel.retrieveCategoryById(i.ingredient.categoryId)
                    if (!detail.checked)
                        shoppingItemList.add(0, ShoppingItem(detail, i.ingredient.ingredientName, ""))
                    else
                        shoppingItemList.add(ShoppingItem(detail, i.ingredient.ingredientName, ""))
                }
            }
            shoppingItemList.let {
                println("submitting shopping list of size : ${it.size}")
                adapter.submitList(it)
            }
            val swipeGesture = object : SwipeGesture(view.context) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    super.onSwiped(viewHolder, direction)
                    when(direction){
                        ItemTouchHelper.LEFT -> {
                            //Delete Item
                            viewModel.deleteShoppingItem(shoppingItemList[viewHolder.adapterPosition].shoppingItemDetail)
                            shoppingItemList.removeAt(viewHolder.adapterPosition)
                        }
                        ItemTouchHelper.RIGHT -> {
                            //Check item
                            println("checking item : ${viewHolder.adapterPosition}")
                            val oldItem = shoppingItemList[viewHolder.adapterPosition]
                            val checkedItem = viewModel.setCheckShoppingItem(oldItem.shoppingItemDetail, true)
                            shoppingItemList.removeAt(viewHolder.adapterPosition)
                            //adapter.notifyItemRemoved(viewHolder.adapterPosition)
                            shoppingItemList.add(ShoppingItem(checkedItem, oldItem.ingredientName, oldItem.categoryName))
                            //adapter.notifyItemInserted(shoppingItemList.size-1)
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


}