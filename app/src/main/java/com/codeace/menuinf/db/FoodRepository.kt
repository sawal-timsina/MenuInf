package com.codeace.menuinf.db

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MediatorLiveData
import com.codeace.menuinf.entity.FoodData
import com.codeace.menuinf.helpers.TAG
import com.codeace.menuinf.helpers.showMessage
import com.codeace.menuinf.workers.DatabaseAsyncTask
import com.codeace.menuinf.workers.SearchAsyncTask
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask

class FoodRepository(application: Application) {
    private val foodImageStorage = FirebaseStorage.getInstance().reference
    private var query: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("foodData")
    private var foodDataDao: FoodDataDao = FoodDatabase.getDatabase(application)!!.foodDataDao()

    private val allFoodData = MediatorLiveData<List<FoodData>>()
    internal var isDataChanged: Boolean = true
    internal val categoryListItems = mutableSetOf<String>()
    internal var maxPrice: Double = 0.0

    init {
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "Inserting Data")
                val fbList: MutableList<FoodData> = mutableListOf()
                dataSnapshot.children.mapNotNullTo(fbList) { it.getValue<FoodData>(FoodData::class.java) }
                /*
                SyncDatabaseAT({
                    Log.d(TAG, "DataBase Inserted")
                    insertDb(it)
                }, {
                    Log.d(TAG, "DataBase Updated")
                    updateDb(it)
                }, {
                    Log.d(TAG, "DataBase Deleted")
                    deleteDb(it)
                }).execute(allFoodData.value, fbList.toList())\
                */
                DatabaseAsyncTask(foodDataDao,4).execute()
                fbList.forEach {
                    insertDb(it)
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
            isDataChanged = true
            Log.d(TAG, "Menu : $list")
            Log.d(TAG, "Refreshing Categories")
            clearItems()
            list.forEach {
                categoryListItems.add(it.food_category)
                maxPrice = maxOf(maxPrice, it.food_price)
            }
            allFoodData.value = list
        }
    }

    private fun clearItems() {
        categoryListItems.clear()
        maxPrice = 0.0
    }

    fun getAllFoodData(): MediatorLiveData<List<FoodData>> {
        return allFoodData
    }

    fun setCurrentList(newList: List<FoodData>) {
        allFoodData.value = newList
    }

    private fun removeSource() {
        allFoodData.removeSource(foodDataDao.getAllFoodData("food_name"))
    }

    fun setDefault() {
        removeSource()
        allFoodData.addSource(foodDataDao.getAllFoodData("food_name")) { list ->
            allFoodData.value = list
            isDataChanged = true
        }
    }

    fun searchItems(word: String) {
        removeSource()
        SearchAsyncTask(foodDataDao, 0) {
            setCurrentList(it)
        }.execute(word)
    }

    fun filterFoodData(min: Int, max: Int, categories: List<String>) {
        removeSource()
        if (categories.isNotEmpty()) {
            SearchAsyncTask(foodDataDao, 1, categories) {
                setCurrentList(it)
            }.execute(min.toString(), max.toString())
        } else if (categories.isEmpty()) {
            SearchAsyncTask(foodDataDao, 1, categoryListItems.toList()) {
                setCurrentList(it)
            }.execute(min.toString(), max.toString())
        } else if (min == 0 && max == maxPrice.toInt() && categories.isEmpty()) {
            setDefault()
        }
    }

    fun insert(foodData: FoodData, email: String, context: Context) {
        foodData.id =
            if (allFoodData.value!!.isEmpty()) 0 else allFoodData.value!!.last().id?.plus(1)
        uploadPicture(foodData, email.plus("_${foodData.id}"), context)
        isDataChanged = true
    }

    fun update(foodData: FoodData, email: String, context: Context) {
        uploadPicture(foodData, email.plus("_${foodData.id}"), context)
        isDataChanged = true
    }

    fun delete(foodName: String, id: Int, email: String, context: Context) {
        foodImageStorage.child("foodImage/$foodName$id.jpg").delete()
            .addOnSuccessListener {
                query.child(email).removeValue()
                showMessage(context, "Data Deleted Successfully")
            }.addOnFailureListener {
                showMessage(context, it.localizedMessage.toString())
            }
        isDataChanged = true
    }

    private fun uploadPicture(data: FoodData, email: String, context: Context) {
        val imageUri = foodImageStorage.child("foodImage/${data.food_name}${data.id}.jpg")
        if (data.food_image.contains("https://firebasestorage.googleapis.com", true)) {
            query.child(email).setValue(data)
            showMessage(context, "Data Updated Successfully")
        } else {
            imageUri.putFile(Uri.parse(data.food_image))
                .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        throw task.exception!!
                    }
                    return@Continuation imageUri.downloadUrl
                })
                .addOnCompleteListener { taskSnapshot ->
                    if (taskSnapshot.isSuccessful) {
                        data.food_image = taskSnapshot.result.toString()
                        query.child(email).setValue(data)
                        showMessage(context, "Data Inserted Successfully")
                    } else {
                        showMessage(
                            context,
                            taskSnapshot.exception?.localizedMessage.toString()
                        )
                    }
                }
        }
    }

    fun insertDb(foodData: FoodData) {
        DatabaseAsyncTask(foodDataDao, 1).execute(foodData)
    }

    fun updateDb(foodData: FoodData) {
        DatabaseAsyncTask(foodDataDao, 2).execute(foodData)
    }

    fun deleteDb(foodData: FoodData) {
        DatabaseAsyncTask(foodDataDao, 3).execute(foodData)
    }
}