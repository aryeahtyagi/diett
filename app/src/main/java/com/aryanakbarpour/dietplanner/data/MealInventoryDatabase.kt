package com.aryanakbarpour.dietplanner.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [IngredientCategory::class, Ingredient::class,
                      InventoryItemDetail::class, ShoppingItemDetail::class,
                      Cuisine::class, FoodType::class, Diet::class, RecipeDetail::class, RecipeIngredientDetail::class],
          version = 8, exportSchema = false)
@TypeConverters(Converters::class)
abstract class MealInventoryDatabase : RoomDatabase() {
    abstract fun ingredientDao(): IngredientDao

    abstract fun inventoryDao(): InventoryDao

    abstract fun shoppingDao(): ShoppingDao

    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile
        private var INSTANCE: MealInventoryDatabase? = null

        fun getDatabase(context: Context): MealInventoryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MealInventoryDatabase::class.java,
                    "meal_inventory_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}