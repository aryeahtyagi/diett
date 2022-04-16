package com.atriasoft.dietplanner.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import com.atriasoft.dietplanner.data.*
import kotlinx.coroutines.launch

class RecipeViewModel (private val recipeDao: RecipeDao, private val ingredientDao: IngredientDao, private val inventoryDao: InventoryDao) : ViewModel() {

    fun insertRecipe(
        dietName: String, foodTypeName: String, cuisineName: String, ingredientsList: List<RecipeIngredientModel>,
        title: String, servings: String, imageUri: Uri?, instruction: String, prepTime: Int, calories: Double) {

        viewModelScope.launch {
            val dietCheck = recipeDao.getDietIdFromName(dietName)
            val dietId = if (dietCheck.isEmpty()) {
                val newDiet = Diet(name = dietName)
                recipeDao.insertDiet(newDiet)
            } else
                dietCheck[0]

            val foodTypeCheck = recipeDao.getFoodTypeIdFromName(foodTypeName)
            val foodTypeId = if (foodTypeCheck.isEmpty()) {
                val newFoodType = FoodType(name = foodTypeName)
                recipeDao.insertFoodType(newFoodType)
            } else
                foodTypeCheck[0]

            val cuisineCheck = recipeDao.getCuisineIdFromName(cuisineName)
            val cuisineId = if (cuisineCheck.isEmpty()) {
                val newCuisine = Cuisine(name = cuisineName)
                recipeDao.insertCuisine(newCuisine)
            } else
                cuisineCheck[0]

            val imageString : String? = if (imageUri == null){
                null
            } else{
                imageUri.toString()
            }

            val newRecipeDetail = RecipeDetail(
                title = title,
                prepTime = prepTime,
                calories = calories,
                instruction = instruction,
                servings = servings.toInt(),
                image = imageString,
                cuisineId = cuisineId,
                dietId = dietId,
                typeId = foodTypeId
            )
            val recipeId = recipeDao.insertRecipe(newRecipeDetail)

            // Insert recipe ingredients
            for (ingredientItem in ingredientsList) {
                if (ingredientItem.deleted) {
                    println("found deleted found deleted found deleted found deleted")
                    continue

                }

                // check category and add new if required
                val categoryCheck = ingredientDao.getCategoryIdFromName(ingredientItem.category)
                val categoryId = if (categoryCheck.isEmpty()) {
                    val newIngredientCategory = IngredientCategory(categoryName = ingredientItem.category)
                    ingredientDao.insertIngredientCat(newIngredientCategory)
                } else
                    categoryCheck[0]

                // check ingredient and add new if required
                val ingredientCheck = ingredientDao.getIngredientIdFromName(ingredientItem.ingredient)
                val ingredientId = if (ingredientCheck.isEmpty()) {
                    val newIngredient = Ingredient(categoryId = categoryId, ingredientName = ingredientItem.ingredient)
                    ingredientDao.insertIngredient(newIngredient)
                } else
                    ingredientCheck[0]

                val newRecipeIngredientDetail = RecipeIngredientDetail (
                    recipeId = recipeId, ingredientId = ingredientId, amount = ingredientItem.amount
                )
                recipeDao.insertRecipeIngredient(newRecipeIngredientDetail)
            }
        }


    }

    fun createRecipeIngredientMode(category: String, ingredient: String, amount: String) : RecipeIngredientModel {
        return RecipeIngredientModel(category, ingredient, amount)
    }

    fun retrieveAllRecipes(): LiveData<List<Recipe>> {
        return recipeDao.getAllRecipes().asLiveData()
    }
    // Auto Complete Stuff

    fun retrieveRecipeById(id: Long) : LiveData<Recipe> {
        return recipeDao.getRecipeById(id).asLiveData()
    }

    fun retrieveCuisines(): LiveData<List<Cuisine>> {
        return recipeDao.getAllCuisines().asLiveData()
    }

    fun retrieveDiets() : LiveData<List<Diet>> {
        return recipeDao.getAllDiets().asLiveData()
    }

    fun retrieveFoodTypes() : LiveData<List<FoodType>> {
        return recipeDao.getAllFoodTypes().asLiveData()
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

    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch {
            for (recipeIngredient in recipe.ingredients){
                recipeDao.deleteRecipeIngredientDetail(recipeIngredient.recipeIngredientDetail)
            }
            recipeDao.deleteRecipeDetail(recipe.recipe)
        }
    }

    fun inventoryProcessRecipeIngredients(recipeIngredients: List<RecipeIngredient>) {
        viewModelScope.launch {
            for (ri in recipeIngredients) {
                val inventoryItems = inventoryDao.getIngredientInventoryItemsByIngredientId(ri.ingredient.id)
                // check inventory items available

                if (!inventoryItems.inventoryItemDetails.isEmpty()){
                    // find the right inventory item
                    var bestItemIndex = -1
                    for ((index, invItem) in inventoryItems.inventoryItemDetails.withIndex()) {
                        val recipeIngUnit = ri.recipeIngredientDetail.amount.split(' ')[1]
                        val inventoryIngUnit = invItem.quantity.split(' ')[1]

                        val recipeValue = ri.recipeIngredientDetail.amount.split(' ')[0].toDouble()
                        val inventoryValue = invItem.quantity.split(' ')[0].toDouble()


                        if(recipeIngUnit == inventoryIngUnit){
                            if (bestItemIndex < 0) {
                                bestItemIndex = index
                                continue
                            }

                            val bestItemValue = inventoryItems.inventoryItemDetails[bestItemIndex].quantity.split(' ')[0].toDouble()
                            val currentBestExpiry = inventoryItems.inventoryItemDetails[bestItemIndex].expiry

                            if (recipeValue < inventoryValue) {
                                if (currentBestExpiry.after(invItem.expiry) || bestItemValue <= recipeValue) {
                                    bestItemIndex = index
                                }
                            } else {
                                if (currentBestExpiry.after(invItem.expiry) && bestItemValue < recipeValue) {
                                    bestItemIndex = index
                                }
                            }

                        }
                    }
                    // best a suitable item found
                    if (bestItemIndex >= 0) {
                        val inventoryValue = inventoryItems.inventoryItemDetails[bestItemIndex].quantity.split(' ')[0].toDouble()
                        val recipeValue = ri.recipeIngredientDetail.amount.split(' ')[0].toDouble()
                        val newValue = inventoryValue - recipeValue

                        if ( newValue <= 0.0) {
                            // Delete inventory item
                            inventoryDao.deleteInventoryItem(inventoryItems.inventoryItemDetails[bestItemIndex])
                        } else {
                            // Update inventory item

                            val unitString = ri.recipeIngredientDetail.amount.split(' ')[1]
                            val newValueString =  if (newValue % 1.0 < 0.01)
                                newValue.toInt()
                            else
                                newValue

                            val quantityString = "$newValueString $unitString"

                            val newInventoryItemDetail = InventoryItemDetail(
                                id = inventoryItems.inventoryItemDetails[bestItemIndex].id,
                                quantity =quantityString,
                                expiry = inventoryItems.inventoryItemDetails[bestItemIndex].expiry,
                                ingredientId = ri.ingredient.id,
                                isFrozen = inventoryItems.inventoryItemDetails[bestItemIndex].isFrozen
                            )

                            inventoryDao.updateInventoryItem(newInventoryItemDetail)
                        }


                    }
                }
            }
        }
    }
}

data class RecipeIngredientModel(
    val category: String,
    val ingredient: String,
    val amount: String,
    var deleted: Boolean = false
)

class RecipeViewModelFactory(private val recipeDao: RecipeDao, private val ingredientDao: IngredientDao, private val inventoryDao: InventoryDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecipeViewModel(recipeDao, ingredientDao, inventoryDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}