package com.codeace.menuinf.dataHolders

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.codeace.menuinf.foodData.FoodData

class FoodViewModel(application: Application) : AndroidViewModel(application) {

    private val foodRepository: FoodRepository =
        FoodRepository(application)

    internal var allFoodData: LiveData<List<FoodData>>

    internal val categoryListItems = mutableSetOf<String>()

    internal val selectedCategories = mutableListOf<Int>()

    internal var _maxPrice: Double = 0.0

    init {
        allFoodData = foodRepository.allFoodData
    }

//    fun setFoodDataList(foodData: List<FoodData>){
//        allFoodData.value = foodData
//    }

//    fun setDefaults(){
//        allFoodData = MutableLiveData<List<FoodData>>()
//    }

    fun getCategories() {
        categoryListItems.clear()
        allFoodData.value?.forEach {
            categoryListItems.add(it.category)
            _maxPrice = maxOf(_maxPrice, it.price)
        }
    }

    fun filterByData(minPrice: Double = 0.0, maxPrice: Double = 0.0): List<FoodData> {
        val listItem: MutableList<FoodData> = mutableListOf()

        if(!selectedCategories.isNullOrEmpty()){
            selectedCategories.forEach {
                allFoodData.value?.filter { s -> s.category == categoryListItems.toList()[it] && s.price in minPrice..maxPrice }
                    ?.forEach {
                        listItem.add(it)
                    }
            }
        } else {
            allFoodData.value?.filter { s -> s.price in minPrice..maxPrice }
                ?.forEach {
                    listItem.add(it)
                }
        }

        return if (listItem.isNullOrEmpty()) allFoodData.value!! else listItem
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