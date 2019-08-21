package com.codeace.menuinf.db

import android.app.Application
import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.MediatorLiveData
import com.codeace.menuinf.entity.FoodData
import com.codeace.menuinf.helpers.getListener
import com.codeace.menuinf.helpers.sort
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FoodRepository(application: Application) {
    var query: DatabaseReference = FirebaseDatabase.getInstance().reference.child("foodData")

    var foodDataDao: FoodDataDao = FoodDatabase.getDatabase(application)!!.foodDataDao()
    private val allFoodData = MediatorLiveData<List<FoodData>>()

    private val listener = getListener { dataSnapshot ->
        val list: MutableList<FoodData> = mutableListOf()
        dataSnapshot.children.forEach {
            val foodData = it.getValue(FoodData::class.java)!!
            sort(list, foodData)
        }
        list.forEach {
            insertDb(it)
        }
    }

    init {
        query.addValueEventListener(listener)
        allFoodData.addSource(foodDataDao.allFoodData) { list -> allFoodData.setValue(list) }
    }

    fun insert(foodData: FoodData, email: String) {
        query.child(email).setValue(foodData)
    }

    fun delete(email: String) {
        query.child(email).removeValue()
    }

    fun getAllFoodData(): MediatorLiveData<List<FoodData>> {
        return allFoodData
    }

    fun insertDb(foodData: FoodData) {
        DatabaseAsyncTask(foodDataDao, 1).execute(foodData)
    }

    fun updateDb(foodData: FoodData) {
//        uploadPicture(foodData)
//        DatabaseAsyncTask(foodDataDao, 2).execute(foodData)
    }

    fun deleteDb(foodData: FoodData, pos: Int) {

        Log.d("DBDelete", foodData.toString())
//        DatabaseAsyncTask(foodDataDao, 3).execute(foodData)
    }

    fun deleteAll() {
//        DatabaseAsyncTask(foodDataDao, 4).execute()
    }

    private class DatabaseAsyncTask internal constructor(
        private val mAsyncTaskDao: FoodDataDao,
        private val type: Int
    ) : AsyncTask<FoodData, Void, Void>() {
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