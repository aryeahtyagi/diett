package com.atriasoft.dietplanner.data

import androidx.room.*
import java.util.*

@Entity(tableName = "inventory_item", foreignKeys = [
    ForeignKey(
        entity = Ingredient::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("ingredientId"),
        onDelete = ForeignKey.CASCADE
    )
])
data class InventoryItemDetail(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(index = true)
    val ingredientId: Long,
    val quantity: String,
    val expiry: Date,
    val isFrozen: Boolean
)

data class IngredientInventoryItems(
    @Embedded val ingredient: Ingredient,
    @Relation(
        parentColumn = "id",
        entityColumn = "ingredientId"
    )
    val inventoryItemDetails: List<InventoryItemDetail>
)

data class InventoryItem(
    @Embedded val inventoryItemDetail: InventoryItemDetail,
    @Relation(
        parentColumn = "ingredientId",
        entityColumn = "id"
    )
    val ingredient: Ingredient
)


