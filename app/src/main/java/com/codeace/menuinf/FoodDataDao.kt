package com.codeace.menuinf

import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FoodDataDao {

    @get:Query("SELECT * from food_items ORDER BY food_name ASC")
    val allFoodData: MutableLiveData<MutableList<FoodData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(foodData: FoodData)

    @Query("DELETE FROM food_items")
    fun deleteAll()
}