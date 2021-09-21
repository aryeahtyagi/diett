package com.aryanakbarpour.dietplanner.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingDao {

    // Shopping list functions
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertShoppingItem(shoppingItemDetail: ShoppingItemDetail) : Long

    @Delete
    suspend fun deleteShoppingItem(shoppingItemDetail: ShoppingItemDetail)

    @Update
    suspend fun updateShoppingItemDetail(shoppingItemDetail: ShoppingItemDetail)

    @Query("SELECT * FROM shopping_item")
    fun getShoppingItemDetails() : Flow<List<ShoppingItemDetail>>

    @Transaction
    @Query("SELECT * FROM ingredient")
    fun getIngredientShoppingItems(): Flow<List<IngredientShoppingItem>>


}