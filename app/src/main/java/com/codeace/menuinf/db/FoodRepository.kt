package com.codeace.menuinf.db

import android.app.Application
import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.MediatorLiveData
import com.codeace.menuinf.entity.FoodData
import com.codeace.menuinf.helpers.TAG
import com.google.firebase.database.*

class FoodRepository(application: Application) {
    var query: DatabaseReference = FirebaseDatabase.getInstance().reference.child("foodData")

    var foodDataDao: FoodDataDao = FoodDatabase.getDatabase(application)!!.foodDataDao()
    private val allFoodData = MediatorLiveData<List<FoodData>>()
    var menu: MutableList<FoodData> = mutableListOf()
    internal var isDataChanged: Boolean = true

    init {
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "Inserting Data")
                dataSnapshot.children.forEach {
                    insertDb(it.getValue(FoodData::class.java)!!)
                }
                Log.d(TAG, "Data Inserted")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(
                    "FirebaseQueryLiveData",
                    "Can't listen to query ",
                    databaseError.toException()
                )
            }
        })

        Log.d(TAG, "New Data Source Added")
        allFoodData.addSource(foodDataDao.getAllFoodData("food_name")) { list ->
            allFoodData.value = list
            menu = list.toMutableList()
            isDataChanged = true
            Log.d(TAG, menu.toString())
        }
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