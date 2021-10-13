package com.aryanakbarpour.dietplanner.data

import androidx.room.*

@Entity(tableName = "cuisine")
data class Cuisine(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String
)

@Entity(tableName = "food_type")
data class FoodType(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String
)

@Entity(tableName = "diet")
data class Diet(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String
)

@Entity(tableName = "recipe", foreignKeys = [
    ForeignKey(
        entity = Diet::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("dietId"),
        onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
        entity = FoodType::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("typeId"),
        onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
        entity = Cuisine::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("cuisineId"),
        onDelete = ForeignKey.CASCADE
    )
])
data class RecipeDetail(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val prepTime: String,
    val calories: Double,
    val instruction: String,
    val servings: Int,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val image: ByteArray? = null,
    @ColumnInfo(index = true)
    val dietId: Long,
    @ColumnInfo(index = true)
    val typeId: Long,
    @ColumnInfo(index = true)
    val cuisineId: Long
)

@Entity(tableName = "recipe_ingredient",foreignKeys = [
    ForeignKey(
        entity = RecipeDetail::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("recipeId"),
        onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
        entity = Ingredient::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("ingredientId"),
        onDelete = ForeignKey.CASCADE
    )])
data class RecipeIngredientDetail(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recipeId: Long,
    val ingredientId: Long,
    val amount: String
)

data class RecipeIngredient(
    @Embedded val recipeIngredientDetail: RecipeIngredientDetail,
    @Relation(
        parentColumn = "ingredientId",
        entityColumn = "id"
    )
    val ingredient: Ingredient
)

data class Recipe(
    @Embedded val recipe: RecipeDetail,

    @Relation(
        entity = RecipeIngredientDetail::class,
        parentColumn = "id",
        entityColumn = "recipeId",
    )
    val ingredients: List<RecipeIngredient>,

    @Relation(
        parentColumn = "dietId",
        entityColumn = "id"
    )
    val diet: Diet,

    @Relation(
        parentColumn = "typeId",
        entityColumn = "id"
    )
    val foodType: FoodType,

    @Relation(
        parentColumn = "cuisineId",
        entityColumn = "id"
    )
    val cuisine: Cuisine
)

data class RecipesByCuisine(
    @Embedded val cuisine: Cuisine,
    @Relation(
        parentColumn = "id",
        entityColumn = "cuisineId"
    )
    val recipes: List<RecipeDetail>
)
