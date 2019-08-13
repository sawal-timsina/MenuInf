package com.codeace.menuinf.dataHolders

import android.app.Application
import android.content.ContentValues.TAG
import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.codeace.menuinf.foodData.FoodData
import com.codeace.menuinf.foodData.FoodDataDao
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FoodRepository internal constructor(application: Application) {
//    private val foodDataDao: FoodDataDao
    private val database = FirebaseDatabase.getInstance().reference
    private val menu: MutableList<FoodData> = mutableListOf()
    internal var allFoodData: MutableLiveData<List<FoodData>> = MutableLiveData()

    val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI
            menu.clear()
            dataSnapshot.children.mapNotNullTo(menu) { it.getValue(FoodData::class.java) }
            setFoodDataList(menu)
            Log.d(TAG, "Success setFoodData")
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Post failed, log a message
            Log.d(TAG, "loadPost:onCancelled", databaseError.toException())
        }
    }

    init {
        database.child("foodData").addValueEventListener(postListener)
//        val db = FoodDatabase.getDatabase(application)
//        foodDataDao = db!!.foodDataDao()
    }

    fun setDefault(){
        setFoodDataList(menu)
    }

    fun setFoodDataList(foodData: List<FoodData>) {
        allFoodData.value = foodData
    }

    fun getMenu(): List<FoodData>{
        return menu
    }

    fun insert(foodData: FoodData) {
        foodData.id = menu.last().id?.plus(1)
        database.child("foodData").child(foodData.id.toString()).setValue(foodData)
        Log.d("DBAdd", menu.toString())
//        apiServiceFactory.insertFoodData(foodData)
//        DatabaseAsyncTask(foodDataDao, 1).execute(foodData)
    }

    fun update(foodData: FoodData) {
        database.child("foodData").child(foodData.id.toString()).setValue(foodData)
        Log.d("DBUpdate", menu.toString())
//        DatabaseAsyncTask(foodDataDao, 2).execute(foodData)
    }

    fun delete(foodData: FoodData) {
        database.child("foodData").child(foodData.id.toString()).removeValue()
        Log.d("DBDelete", menu.toString())
//        DatabaseAsyncTask(foodDataDao, 3).execute(foodData)
    }

    fun deleteAll() {
//        DatabaseAsyncTask(foodDataDao, 4).execute()
    }

    fun insertAll(resultModel: List<FoodData>) {
        resultModel.forEach {
//            DatabaseAsyncTask(foodDataDao, 1).execute(it)
        }
    }

    private class DatabaseAsyncTask internal constructor(private val mAsyncTaskDao: FoodDataDao, private val type: Int) : AsyncTask<FoodData, Void, Void>() {
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