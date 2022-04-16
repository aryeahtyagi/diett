package com.atriasoft.dietplanner.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingDao {

    // Shopping list functions
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertShoppingItem(shoppingItemDetail: ShoppingItemDetail) : Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertInventoryItem(inventoryItemDetail: InventoryItemDetail) : Long

    @Delete
    suspend fun deleteShoppingItem(shoppingItemDetail: ShoppingItemDetail)

    @Update
    suspend fun updateShoppingItemDetail(shoppingItemDetail: ShoppingItemDetail)

    @Query("SELECT * FROM shopping_item")
    fun getShoppingItemDetails() : Flow<List<ShoppingItemDetail>>

    @Transaction
    @Query("SELECT * FROM shopping_item WHERE checked=1")
    fun getMarkedShoppingItems(): Flow<List<ShoppingItem>>

    @Transaction
    @Query("SELECT * FROM shopping_item ORDER BY checked")
    fun getShoppingItems(): Flow<List<ShoppingItem>>

    @Transaction
    @Query("SELECT * FROM shopping_item WHERE id=:id")
    fun getShoppingItemById(id: Long): Flow<List<ShoppingItem>>


}