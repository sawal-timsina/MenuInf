package com.codeace.menuinf

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData

class FoodReprository internal constructor(application: Application) {

    private val foodDataDao: FoodDataDao
    internal val allWords: LiveData<ArrayList<FoodData>>

    init {
        val db = FoodDatabase.getDatabase(application)
        foodDataDao = db!!.foodDataDao()
        allWords = foodDataDao.allFoodData
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