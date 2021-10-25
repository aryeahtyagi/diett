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

data class ShoppingItem (
    @Embedded
    val shoppingItemDetail: ShoppingItemDetail,
    @Relation(
        entity = Ingredient::class,
        parentColumn = "ingredientId",
        entityColumn = "id"
    )
    val ingredient: IngredientWithCategory,
)