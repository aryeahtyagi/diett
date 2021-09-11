package com.aryanakbarpour.dietplanner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aryanakbarpour.dietplanner.data.IngredientDao
import com.aryanakbarpour.dietplanner.data.InventoryDao

class InventoryViewModel(private val ingredientDao: IngredientDao, private val inventoryDao: InventoryDao) : ViewModel() {

}

class InventoryViewModelFactory(private val ingredientDao: IngredientDao, private val inventoryDao: InventoryDao) :ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InventoryViewModel(ingredientDao, inventoryDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}