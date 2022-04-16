package com.atriasoft.dietplanner.viewmodel

import androidx.lifecycle.*
import com.atriasoft.dietplanner.data.*
import kotlinx.coroutines.launch
import java.util.*

class InventoryViewModel(private val ingredientDao: IngredientDao,
                         private val inventoryDao: InventoryDao) : ViewModel() {

    /**
     * Calls ingredient and inventory Daos to insert an InventoryItem into the database from raw values. Inserts
     * predecessors entry if ingredientName and ingredientCategoryName are not present.
     */
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

    fun retrieveIngredientById(id: Long): LiveData<Ingredient> {
        return ingredientDao.getIngredientById(id).asLiveData()
    }

    fun deleteInventoryItemById(id: Long) {
        viewModelScope.launch {
            inventoryDao.deleteInventoryItemById(id)
        }
    }

    fun updateInventoryItemDetail(itemDetail: InventoryItemDetail) {
        viewModelScope.launch {
            inventoryDao.updateInventoryItem(itemDetail)
        }
    }

    /**
     * Retrieve an inventory item detail by id
     */
    fun retrieveItemDetailById(id: Long): LiveData<InventoryItemDetail> {
        return inventoryDao.getInventoryItemDetailById(id).asLiveData()
    }

    /**
     * Retrieve list of all inventory items
     */
    fun retrieveAllInventoryItems(): LiveData<List<InventoryItem>> {
        return inventoryDao.getInventoryItems().asLiveData()
    }

    /**
     * Retrieve list of all CategorisedIngredients(s)
     */
    fun retrieveCategorisedIngredients(): LiveData<List<CategorisedIngredients>> {
        return ingredientDao.getCategorisedIngredients().asLiveData()
    }

    /**
     * Retrieve list of all Ingredient(s)
     */
    fun retrieveIngredients(): LiveData<List<Ingredient>> {
        return ingredientDao.getAllIngredients().asLiveData()
    }

    /**
     * Retrieve a (list) of IngredientCategory from name
     */
    fun retrieveCategoryFromIngredientName(name: String): LiveData<List<IngredientCategory>> {
        return ingredientDao.getCategoryFromIngredientName(name).asLiveData()
    }

    /**
     * Retrieve list of all IngredientCategory(s)
     */
    fun retrieveIngredientCategories(): LiveData<List<IngredientCategory>> {
        return ingredientDao.getAllIngredientCategories().asLiveData()
    }

    /**
     * Calls insertInventoryItem(quantity: String, expiryDate: Date, frozen: Boolean,
     * ingredientName: String, ingredientCategoryName: String)
     */
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