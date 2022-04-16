package com.atriasoft.dietplanner

import android.app.Application
import com.atriasoft.dietplanner.data.MealInventoryDatabase

class DietPlannerApplication : Application(){
    val database: MealInventoryDatabase by lazy { MealInventoryDatabase.getDatabase(this) }
}