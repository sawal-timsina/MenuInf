package com.codeace.menuinf.dataHolders

import android.content.ContentValues.TAG
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.codeace.menuinf.foodData.FoodData
import com.codeace.menuinf.foodData.FoodDataDao
import com.codeace.menuinf.helpers.sort
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask

class FoodRepository {
//    private val foodDataDao: FoodDataDao
    private val database = FirebaseDatabase.getInstance().reference
    private val foodImageStorage = FirebaseStorage.getInstance().reference
    private val menu: MutableList<FoodData> = mutableListOf()
    internal var allFoodData: MutableLiveData<List<FoodData>> = MutableLiveData()

    private val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI
            menu.clear()
            dataSnapshot.children.forEach {
                sort(menu, it.getValue(FoodData::class.java)!!)
            }
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

    fun setDefault() {
        setFoodDataList(menu)
    }

    fun setFoodDataList(foodData: List<FoodData>) {
        allFoodData.value = foodData
    }

    fun getMenu(): List<FoodData> {
        return menu
    }

    fun insert(foodData: FoodData) {
        foodData.id = menu.last().id?.plus(1)
        uploadPicture(foodData)

        Log.d("DBAdd", menu.toString())
//        apiServiceFactory.insertFoodData(foodData)
//        DatabaseAsyncTask(foodDataDao, 1).execute(foodData)
    }

    fun update(foodData: FoodData) {
        uploadPicture(foodData)
        Log.d("DBUpdate", menu.toString())
//        DatabaseAsyncTask(foodDataDao, 2).execute(foodData)
    }

    fun delete(foodData: FoodData) {
        foodImageStorage.child("foodImage/${foodData.food_name}${foodData.id}.jpg").delete()
            .addOnSuccessListener {
                database.child("foodData").child(foodData.id.toString()).removeValue()
                Log.d("FoodI", "Image Deleted")
            }.addOnFailureListener {
                Log.d("FoodI", it.localizedMessage.toString())
            }

        Log.d("DBDelete", foodData.toString())
//        DatabaseAsyncTask(foodDataDao, 3).execute(foodData)
    }

    fun deleteAll() {
//        DatabaseAsyncTask(foodDataDao, 4).execute()
    }

    private fun uploadPicture(data: FoodData) {
        val imageUri = foodImageStorage.child("foodImage/${data.food_name}${data.id}.jpg")
        imageUri.putFile(Uri.parse(data.food_image))
            .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }
                return@Continuation imageUri.downloadUrl
            })
            .addOnCompleteListener { taskSnapshot ->
                // Get a URL to the uploaded content
                if (taskSnapshot.isSuccessful) {
                    data.food_image = taskSnapshot.result.toString()
                    database.child("foodData").child(data.id.toString()).setValue(data)
                    Log.d("FoodI", taskSnapshot.result.toString())
                    return@addOnCompleteListener
                } else {
                    Log.d("FoodF", taskSnapshot.exception?.localizedMessage.toString())
                }
            }

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