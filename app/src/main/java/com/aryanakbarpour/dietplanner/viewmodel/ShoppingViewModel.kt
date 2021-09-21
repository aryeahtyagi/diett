package com.aryanakbarpour.dietplanner.viewmodel

import androidx.lifecycle.*
import com.aryanakbarpour.dietplanner.data.*
import kotlinx.coroutines.launch

class ShoppingViewModel (private val shoppingDao: ShoppingDao, private val ingredientDao: IngredientDao) : ViewModel(){

    private fun insertShoppingItem(ingredientCategoryName: String, ingredientName: String, quantityString: String) {
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
            val newItemDetail = ShoppingItemDetail(
                ingredientId = ingredientId,
                quantity = quantityString,
                checked = false
            )
            shoppingDao.insertShoppingItem(newItemDetail)
        }
    }

    private fun updateShoppingItemDetail(shoppingItemDetail: ShoppingItemDetail) {
        viewModelScope.launch {
            shoppingDao.updateShoppingItemDetail(shoppingItemDetail)
        }
    }

    private fun createShoppingItemDetailEntry(ingredientId: Long, quantityString: String, state: Boolean) : ShoppingItemDetail{
        return ShoppingItemDetail(
            ingredientId = ingredientId,
            quantity = quantityString,
            checked = state
        )
    }

    fun deleteShoppingItem(shoppingItemDetail: ShoppingItemDetail) {
        viewModelScope.launch { shoppingDao.deleteShoppingItem(shoppingItemDetail) }
    }

    fun setCheckShoppingItem(shoppingItemDetail: ShoppingItemDetail, state: Boolean) : ShoppingItemDetail{
        val newItemDetail = ShoppingItemDetail(
            id = shoppingItemDetail.id,
            ingredientId = shoppingItemDetail.ingredientId,
            quantity = shoppingItemDetail.quantity,
            checked = state)
        updateShoppingItemDetail(newItemDetail)

        return newItemDetail
    }

    fun retrieveIngredientShoppingItems() : LiveData<List<IngredientShoppingItem>> {
        return shoppingDao.getIngredientShoppingItems().asLiveData()
    }


    fun retrieveCategoryById(id: Long) : IngredientCategory {
        return ingredientDao.getCategoryById(id)
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

    fun addNewShoppingItem(categoryName: String, ingredientName: String, quantityString: String){
        insertShoppingItem(categoryName, ingredientName, quantityString)
    }
}

class ShoppingViewModelFactory(private val shoppingDao: ShoppingDao, private val ingredientDao: IngredientDao) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShoppingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShoppingViewModel(shoppingDao, ingredientDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}