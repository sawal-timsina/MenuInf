package com.codeace.menuinf

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class FoodViewModel(application: Application) : AndroidViewModel(application) {

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

    fun delete(foodData: FoodData) {
        foodRepository.delete(foodData)
    }

    fun deleteAll() {
        foodRepository.deleteAll()
    }
}