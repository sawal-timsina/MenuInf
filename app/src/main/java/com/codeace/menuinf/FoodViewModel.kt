package com.codeace.menuinf

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class FoodViewModel(application: Application) : ViewModel() {

    private val foodRepository: FoodRepository = FoodRepository(application)

    internal val allFoodData: LiveData<MutableList<FoodData>>

    init {
        allFoodData = foodRepository.allFoodData
    }

    fun insert(foodData: FoodData) {
        foodRepository.insert(foodData)
    }
}