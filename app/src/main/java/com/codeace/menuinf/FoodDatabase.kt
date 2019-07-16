package com.codeace.menuinf

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [FoodData::class], version = 1)
abstract class FoodDatabase : RoomDatabase() {
    abstract fun foodDataDao(): FoodDataDao

}