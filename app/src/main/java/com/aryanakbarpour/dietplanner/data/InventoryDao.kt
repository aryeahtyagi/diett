package com.aryanakbarpour.dietplanner.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertInventoryItem(inventoryItemDetail: InventoryItemDetail) : Long

    @Update
    suspend fun updateInventoryItem(inventoryItemDetail: InventoryItemDetail)

    @Delete
    suspend fun deleteInventoryItem(inventoryItemDetail: InventoryItemDetail)

    @Query("DELETE FROM inventory_item WHERE id = :id")
    suspend fun deleteInventoryItemById(id: Long)

    @Query("SELECT * FROM inventory_item WHERE id = :id")
    fun getInventoryItemDetailById(id: Long): Flow<InventoryItemDetail>

    @Transaction
    @Query("SELECT * FROM ingredient")
    fun getIngredientInventoryItems(): Flow<List<IngredientInventoryItems>>


}