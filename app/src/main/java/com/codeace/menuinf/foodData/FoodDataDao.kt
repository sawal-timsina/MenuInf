package com.codeace.menuinf.foodData

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FoodDataDao {

    @get:Query("SELECT * from food_items ORDER BY food_name ASC")
    val allFoodData: LiveData<List<FoodData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(foodData: FoodData)

    @Update
    fun update(foodData: FoodData)

    @Delete
    fun delete(foodData: FoodData)

    @Query("DELETE FROM food_items")
    fun deleteAll()
}