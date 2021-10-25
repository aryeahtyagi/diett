package com.aryanakbarpour.dietplanner.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIngredientCat(ingredientCategory: IngredientCategory) : Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIngredient(ingredient: Ingredient) : Long

    @Query("SELECT id FROM ingredient_category WHERE categoryName = :name")
    suspend fun getCategoryIdFromName(name: String): List<Long>

    @Query("SELECT id FROM ingredient WHERE ingredientName = :name")
    suspend fun getIngredientIdFromName(name: String): List<Long>

    @Query("SELECT * FROM ingredient WHERE id = :id")
    fun getIngredientById(id: Long): Flow<Ingredient>

    @Query("SELECT * FROM ingredient WHERE ingredientName = :name")
    fun getIngredientFromName(name: String): Flow<List<Ingredient>>

    @Query("SELECT * FROM ingredient WHERE id = :id")
    fun getIngredientFromId(id: Long): Ingredient

    @Query("SELECT * FROM ingredient_category WHERE id = (SELECT categoryId FROM ingredient WHERE ingredientName = :name)")
    fun getCategoryFromIngredientName(name: String) : Flow<List<IngredientCategory>>

    @Query("SELECT * FROM ingredient_category WHERE id = :id")
    fun getCategoryById(id: Long): IngredientCategory

    @Query("SELECT * FROM ingredient")
    fun getAllIngredients() : Flow<List<Ingredient>>

    @Query("SELECT * FROM ingredient_category")
    fun getAllIngredientCategories() : Flow<List<IngredientCategory>>

    @Transaction
    @Query("SELECT * FROM ingredient_category")
    fun getCategorisedIngredients(): Flow<List<CategorisedIngredients>>

    @Transaction
    @Query("SELECT * FROM ingredient")
    fun getIngredientsWithCategory(): Flow<List<IngredientWithCategory>>;

}