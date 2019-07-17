package com.codeace.menuinf

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class FoodViewModel(application: Application) : ViewModel() {

    private val foodRepository: FoodRepository = FoodRepository(application)

    internal val allFoodData: LiveData<List<FoodData>>

    init {
        allFoodData = foodRepository.allFoodData
    }

    fun insert(foodData: FoodData) {
        foodRepository.insert(foodData)
    }

    fun update(foodData: FoodData) {
        foodRepository.update(foodData)
    }

    fun deleteAll() {
        foodRepository.deleteAll()
    }
}