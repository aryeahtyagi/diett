package com.atriasoft.dietplanner.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    // Inserts
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRecipe(recipeDetail: RecipeDetail) : Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDiet(diet: Diet) : Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFoodType(foodType: FoodType) : Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCuisine(cuisine: Cuisine) : Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRecipeIngredient(recipeIngredientDetail: RecipeIngredientDetail) : Long

    // Updates

    //Deletes
    @Delete
    suspend fun deleteRecipeIngredientDetail(recipeIngredientDetail: RecipeIngredientDetail)

    @Delete
    suspend fun deleteRecipeDetail(recipeDetail: RecipeDetail)

    // Gets
    @Query("SELECT id FROM cuisine WHERE name = :name")
    suspend fun getCuisineIdFromName(name: String) : List<Long>

    @Query("SELECT id FROM food_type WHERE name = :name")
    suspend fun getFoodTypeIdFromName(name: String) : List<Long>

    @Query("SELECT id FROM diet WHERE name = :name")
    suspend fun getDietIdFromName(name: String) : List<Long>

    @Query("SELECT * FROM cuisine")
    fun getAllCuisines() : Flow<List<Cuisine>>

    @Query("SELECT * from diet")
    fun getAllDiets() : Flow<List<Diet>>

    @Query("SELECT * from food_type")
    fun getAllFoodTypes() : Flow<List<FoodType>>

    @Transaction
    @Query("SELECT * FROM recipe WHERE id = :id")
    fun getRecipeById(id: Long) : Flow<Recipe>

    @Transaction
    @Query("SELECT * FROM recipe")
    fun getAllRecipes(): Flow<List<Recipe>>

    @Transaction
    @Query("SELECT * FROM cuisine")
    fun getCuisineRecipes(): Flow<List<RecipesByCuisine>>
}