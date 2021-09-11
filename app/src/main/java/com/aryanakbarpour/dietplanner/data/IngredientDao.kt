package com.aryanakbarpour.dietplanner.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIngredientCat(ingredientCategory: IngredientCategory)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIngredient(ingredient: Ingredient)

    @Query("SELECT * FROM ingredient WHERE ingredientName = :name")
    fun getIngredientFromName(name: String): Flow<List<Ingredient>>

    @Query("SELECT * FROM ingredient WHERE id = :id")
    fun getIngredientFromId(id: Int): Flow<List<Ingredient>>

    @Transaction
    @Query("SELECT * FROM ingredient_category")
    fun getCategorisedIngredients(): Flow<List<CategorisedIngredients>>

//    @Transaction
//    @Query("SELECT * FROM ingredient WHERE categoryId = (SELECT id FROM ingredient_category WHERE categoryName = :cat_name)")
//    fun getIngredientInCategory(cat_name: String): Flow<List<CategorisedIngredients>>

}