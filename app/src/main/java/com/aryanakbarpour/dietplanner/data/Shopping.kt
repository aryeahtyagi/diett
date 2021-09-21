package com.aryanakbarpour.dietplanner.data

import androidx.room.*

@Entity(tableName = "shopping_item", foreignKeys = [
    ForeignKey(
        entity = Ingredient::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("ingredientId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class ShoppingItemDetail(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(index = true)
    val ingredientId: Long,
    val quantity: String,
    val checked: Boolean
)

data class IngredientShoppingItem(
    @Embedded val ingredient: Ingredient,
    @Relation(
        parentColumn = "id",
        entityColumn = "ingredientId"
    )
    val shoppingItemDetails: List<ShoppingItemDetail>
)

data class ShoppingItem (
    @Embedded
    val shoppingItemDetail: ShoppingItemDetail,
    val ingredientName: String,
    val categoryName: String

)