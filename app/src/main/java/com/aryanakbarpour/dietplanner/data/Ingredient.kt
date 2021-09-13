package com.aryanakbarpour.dietplanner.data

import androidx.room.*

@Entity(tableName = "ingredient_category")
data class IngredientCategory (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val categoryName: String
)

@Entity(tableName = "ingredient", foreignKeys = [
    ForeignKey(
        entity = IngredientCategory::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("categoryId"),
        onDelete = ForeignKey.CASCADE
    )
])
class Ingredient (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val ingredientName: String,
    @ColumnInfo(index = true)
    val categoryId: Long
)

data class CategorisedIngredients(
    @Embedded val ingredientCategory: IngredientCategory,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val ingredients: List<Ingredient>
)