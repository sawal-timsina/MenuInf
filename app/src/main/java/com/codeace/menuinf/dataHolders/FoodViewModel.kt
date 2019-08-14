package com.codeace.menuinf.dataHolders

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.codeace.menuinf.foodData.FoodData

class FoodViewModel(application: Application) : AndroidViewModel(application) {

    private val foodRepository: FoodRepository =
        FoodRepository()

    internal val categoryListItems = mutableSetOf<String>()

    internal val selectedCategories = mutableListOf<Int>()

    internal var _maxPrice: Double = 0.0

    internal var isChanged: Boolean = true

    fun setFoodDataList(foodData: List<FoodData>) {
        foodRepository.setFoodDataList(foodData)
    }

    fun setDefaults() {
        foodRepository.setDefault()
    }

    fun getAllFoodData(): LiveData<List<FoodData>>{
        return foodRepository.allFoodData
    }

    fun getCategories() {
        categoryListItems.clear()
        _maxPrice = 0.0
        foodRepository.getMenu().forEach {
            categoryListItems.add(it.food_category)
            _maxPrice = maxOf(_maxPrice, it.food_price)
        }
    }

    fun filterByData(minPrice: Double = 0.0, maxPrice: Double = 0.0): List<FoodData> {
        val listItem: MutableList<FoodData> = mutableListOf()

        if (!selectedCategories.isNullOrEmpty()) {
            selectedCategories.forEach {
                foodRepository.getMenu().filter { s -> s.food_category == categoryListItems.toList()[it] && s.food_price in minPrice..maxPrice }
                    .forEach {
                        listItem.add(it)
                    }
            }
        } else {
            foodRepository.getMenu().filter { s -> s.food_price in minPrice..maxPrice }.forEach {
                listItem.add(it)
            }
        }

        return if (listItem.isNullOrEmpty())
            foodRepository.allFoodData.value!!
        else
            listItem
    }

    fun insert(foodData: FoodData) {
        foodRepository.insert(foodData)
        isChanged = true
    }

    fun update(foodData: FoodData) {
        foodRepository.update(foodData)
        isChanged = true
    }

    fun delete(foodData: FoodData) {
        foodRepository.delete(foodData)
        isChanged = true
    }

    fun deleteAll() {
        foodRepository.deleteAll()
        isChanged = true
    }
}