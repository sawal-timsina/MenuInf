package com.codeace.menuinf

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData

class FoodRepository internal constructor(application: Application) {

    private val foodDataDao: FoodDataDao
    internal val allFoodData: LiveData<ArrayList<FoodData>>

    init {
        val db = FoodDatabase.getDatabase(application)
        foodDataDao = db!!.foodDataDao()
        allFoodData = foodDataDao.allFoodData
    }


    fun insert(foodData: FoodData) {
        InsertAsyncTask(foodDataDao).execute(foodData)
    }

    private class InsertAsyncTask internal constructor(private val mAsyncTaskDao: FoodDataDao) :
        AsyncTask<FoodData, Void, Void>() {

        override fun doInBackground(vararg params: FoodData): Void? {
            mAsyncTaskDao.insert(params[0])
            return null
        }
    }
}