package com.codeace.menuinf.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.codeace.menuinf.db.FoodRepository
import com.codeace.menuinf.entity.FoodData

class FoodViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FoodRepository(application)
    internal val selectedCategories = mutableListOf<String>()

    fun getCategoryList(): List<String> {
        return repository.categoryListItems.toList()
    }

    fun getMaxPrice(): Int {
        return repository.maxPrice.toInt()
    }

    fun getIsDataChanged(): Boolean {
        return repository.isDataChanged
    }

    fun setIsDataChanged(boolean: Boolean) {
        repository.isDataChanged = boolean
    }

    fun getLiveData(): LiveData<List<FoodData>> {
        return repository.getAllFoodData()
    }

    fun setDefault() {
        repository.setDefault()
        Log.d(com.codeace.menuinf.helpers.TAG, "Default Live Data")
    }

    fun searchItem(text: String) {
        repository.searchItems(text)
    }

    fun filterFoodData(min: Int, max: Int) {
        repository.filterFoodData(min, max, selectedCategories)
    }

    /*fun filterByData(minPrice: Double = 0.0, maxPrice: Double = 0.0): List<FoodData> {
        val listItem: MutableList<FoodData> = mutableListOf()
        if (!selectedCategories.isNullOrEmpty()) {
            selectedCategories.forEach { pos ->
                repository.menu.filter { s -> s.food_category == categoryListItems.toList()[pos] && s.food_price in minPrice..maxPrice }
                    .forEach {
                        listItem.add(it)
                    }
            }
        } else {
            repository.menu.filter { s -> s.food_price in minPrice..maxPrice }
                .forEach {
                    listItem.add(it)
                }
        }
        return listItem
    }*/

    fun insert(foodData: FoodData, email: String) {
        repository.insert(foodData, email, getApplication())
    }

    fun update(foodData: FoodData, email: String) {
        repository.update(foodData, email, getApplication())
    }

    fun delete(foodName: String, id: Int, email: String) {
        repository.delete(foodName, id, email, getApplication())
    }

    fun deleteAll() {
    }
}