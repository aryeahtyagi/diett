package com.aryanakbarpour.dietplanner.viewmodel

import androidx.lifecycle.*
import com.aryanakbarpour.dietplanner.data.*
import kotlinx.coroutines.launch
import java.util.*

class InventoryViewModel(private val ingredientDao: IngredientDao, private val inventoryDao: InventoryDao) : ViewModel() {

    private fun insertInventoryItem(quantity: String, expiryDate: Date, frozen: Boolean,
                                    ingredientName: String, ingredientCategoryName: String) {
        viewModelScope.launch {
            // check category and add new if required
            val categoryCheck = ingredientDao.getCategoryIdFromName(ingredientCategoryName)
            val categoryId = if (categoryCheck.isEmpty()) {
                val newIngredientCategory = IngredientCategory(categoryName = ingredientCategoryName)
                ingredientDao.insertIngredientCat(newIngredientCategory)
            } else
                categoryCheck[0]

            // check ingredient and add new if required
            val ingredientCheck = ingredientDao.getIngredientIdFromName(ingredientName)
            val ingredientId = if (ingredientCheck.isEmpty()) {
                val newIngredient = Ingredient(categoryId = categoryId, ingredientName = ingredientName)
                ingredientDao.insertIngredient(newIngredient)
            } else
                ingredientCheck[0]

            // Insert item
            val newItemDetail = InventoryItemDetail(
                ingredientId = ingredientId,
                quantity = quantity,
                expiry = expiryDate,
                isFrozen = frozen
            )
            inventoryDao.insertInventoryItem(newItemDetail)
        }
    }

    fun retrieveCategorisedIngredients(): LiveData<List<CategorisedIngredients>> {
        return ingredientDao.getCategorisedIngredients().asLiveData()
    }

    fun retrieveIngredients(): LiveData<List<Ingredient>> {
        return ingredientDao.getAllIngredients().asLiveData()
    }

    fun retrieveCategoryFromIngredientName(name: String): LiveData<List<IngredientCategory>> {
        return ingredientDao.getCategoryFromIngredientName(name).asLiveData()
    }

    fun retrieveIngredientCategories(): LiveData<List<IngredientCategory>> {
        return ingredientDao.getAllIngredientCategories().asLiveData()
    }

    fun addNewInventoryItem(quantity: String, expiryDate: Date, frozen: Boolean, ingredientName: String, ingredientCategoryName: String) {
        insertInventoryItem(quantity, expiryDate, frozen, ingredientName, ingredientCategoryName)
    }

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