package com.codeace.menuinf

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData

class FoodRepository internal constructor(application: Application) {

    private val foodDataDao: FoodDataDao
    internal val allFoodData: LiveData<List<FoodData>>

    init {
        val db = FoodDatabase.getDatabase(application)
        foodDataDao = db!!.foodDataDao()
        allFoodData = foodDataDao.allFoodData
    }

    fun insert(foodData: FoodData) {
        DatabaseAsyncTask(foodDataDao, 1).execute(foodData)
    }

    fun update(foodData: FoodData) {
        DatabaseAsyncTask(foodDataDao, 2).execute(foodData)
    }

    fun delete(foodData: FoodData) {
        DatabaseAsyncTask(foodDataDao, 3).execute(foodData)
    }

    fun deleteAll() {
        DatabaseAsyncTask(foodDataDao, 4).execute()
    }

    private class DatabaseAsyncTask internal constructor(
        private val mAsyncTaskDao: FoodDataDao,
        private val type: Int
    ) :
        AsyncTask<FoodData, Void, Void>() {

        override fun doInBackground(vararg params: FoodData): Void? {
            when (type) {
                1 -> {
                    mAsyncTaskDao.insert(params[0])
                }
                2 -> {
                    mAsyncTaskDao.update(params[0])
                }
                3 -> {
                    mAsyncTaskDao.delete(params[0])
                }
                4 -> {
                    mAsyncTaskDao.deleteAll()
                }
            }
            return null
        }
    }
}