package com.codeace.menuinf.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import com.codeace.menuinf.entity.FoodData
import com.codeace.menuinf.helpers.sort
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class FireBaseLiveData(ref: DatabaseReference) : LiveData<List<FoodData>>() {
    private val LOG_TAG = "FirebaseQueryLiveData"
    private val query: DatabaseReference = ref
    private val listener = MyValueEventListener()

    override fun onActive() {
        Log.d(LOG_TAG, "onActive")
        query.addValueEventListener(listener)
    }

    override fun onInactive() {
        Log.d(LOG_TAG, "onInactive")
        query.removeEventListener(listener)
    }

    fun insert(foodData: FoodData) {
        query.child(foodData.id.toString()).setValue(foodData)
        Log.d(LOG_TAG, "data Inserted")
    }

    fun delete(foodData: FoodData) {
        query.child(foodData.id.toString()).removeValue()
    }

    private inner class MyValueEventListener : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val list: MutableList<FoodData> = mutableListOf()
            dataSnapshot.children.forEach {
                sort(list, it.getValue(FoodData::class.java)!!)
            }
            value = list
            Log.d(LOG_TAG, "data Changed")
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.e(LOG_TAG, "Can't listen to query $query", databaseError.toException())
        }
    }

}