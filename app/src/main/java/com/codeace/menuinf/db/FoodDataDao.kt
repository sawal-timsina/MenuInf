package com.codeace.menuinf.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.codeace.menuinf.entity.FoodData

@Dao
interface FoodDataDao {

    @Query("SELECT * from food_items ORDER BY :type ASC")
    fun getAllFoodData(type: String): LiveData<List<FoodData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(foodData: FoodData)

    @Update
    fun update(foodData: FoodData)

    @Delete
    fun delete(foodData: FoodData)

    @Query("DELETE FROM food_items")
    fun deleteAll()

    @Query("SELECT * FROM food_items WHERE food_name == :word ")
    fun searchFoodData(word: String): List<FoodData>

    @Query("SELECT * FROM food_items WHERE food_price BETWEEN :min AND :max AND food_category IN (:categories)")
    fun filterFoodData(min: Int, max: Int, categories: List<String>): List<FoodData>
}