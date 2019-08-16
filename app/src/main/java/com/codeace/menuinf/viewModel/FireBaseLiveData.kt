package com.codeace.menuinf.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import com.codeace.menuinf.entity.FoodData
import com.codeace.menuinf.helpers.sort
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class FireBaseLiveData(private val func: (List<FoodData>) -> Unit) : LiveData<List<FoodData>>() {
    private val LOG_TAG = "FirebaseQueryLiveData"
    var query: DatabaseReference? = null
    private val listener = MyValueEventListener()

    fun setListener() {
        query!!.addValueEventListener(listener)
    }

    fun insert(foodData: FoodData) {
        query!!.child(foodData.id.toString()).setValue(foodData)
    }

    fun delete(foodData: FoodData) {
        query!!.child(foodData.id.toString()).removeValue()
    }

    public override fun setValue(value: List<FoodData>?) {
        super.setValue(value)
    }

    private inner class MyValueEventListener : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val list: MutableList<FoodData> = mutableListOf()
            dataSnapshot.children.forEach {
                sort(list, it.getValue(FoodData::class.java)!!)
            }
            value = list
            func(list)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.e(LOG_TAG, "Can't listen to query $query", databaseError.toException())
        }
    }

}