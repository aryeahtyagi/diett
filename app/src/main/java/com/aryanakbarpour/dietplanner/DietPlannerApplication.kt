package com.aryanakbarpour.dietplanner

import android.app.Application
import com.aryanakbarpour.dietplanner.data.MealInventoryDatabase

class DietPlannerApplication : Application(){
    val database: MealInventoryDatabase by lazy { MealInventoryDatabase.getDatabase(this) }
}