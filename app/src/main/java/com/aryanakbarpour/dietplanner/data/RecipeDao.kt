package com.aryanakbarpour.dietplanner.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRecipe(recipeDetail: RecipeDetail) : Long

    @Transaction
    @Query("SELECT * FROM recipe WHERE id = :id")
    fun getRecipeById(id: Long) : Flow<Recipe>

    @Transaction
    @Query("SELECT * FROM recipe")
    fun getAllRecipes(): Flow<List<Recipe>>

//    @Transaction
//    @Query("SELECT * FROM ingredient")
//    fun getSongsWithPlaylists(): List<RecipesForIngredients>
    @Transaction
    @Query("SELECT * FROM cuisine")
    fun getCuisineRecipes(): Flow<List<RecipesByCuisine>>
}