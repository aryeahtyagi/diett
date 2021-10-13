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

@Entity(primaryKeys = ["recipeId", "ingredientId"])
data class RecipeIngredientCrossRef(
    val recipeId: Long,
    val ingredientId: Long
)

data class Recipe(
    @Embedded val recipe: RecipeDetail,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(RecipeIngredientCrossRef::class, parentColumn = "recipeId", entityColumn = "ingredientId")
    )
    val ingredients: List<Ingredient>,
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
//data class RecipesForIngredients(
//    @Embedded val ingredient: Ingredient,
//    @Relation(
//        parentColumn = "id",
//        entityColumn = "id",
//        associateBy = Junction(RecipeIngredientCrossRef::class, parentColumn = "ingredientId", entityColumn = "recipeId")
//    )
//    val recipes: List<RecipeDetail>
//)