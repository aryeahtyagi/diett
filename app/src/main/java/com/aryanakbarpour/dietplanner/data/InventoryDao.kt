package com.aryanakbarpour.dietplanner.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertInventoryItem(inventoryItemDetail: InventoryItemDetail)

    @Update
    suspend fun updateInventoryItem(inventoryItemDetail: InventoryItemDetail)

    @Delete
    suspend fun deleteInventoryItem(inventoryItemDetail: InventoryItemDetail)

    @Transaction
    @Query("SELECT * FROM ingredient")
    fun getIngredientInventoryItems(): Flow<List<IngredientInventoryItems>>

    // Todo: check if this shit works
    @Transaction
    @Query("SELECT D.*, I.ingredientName FROM inventory_item D, ingredient I WHERE I.id = D.ingredientId")
    fun getInventoryItems(): Flow<List<InventoryItem>>

    // Todo: add sorting and filtering for inventoryItems

}