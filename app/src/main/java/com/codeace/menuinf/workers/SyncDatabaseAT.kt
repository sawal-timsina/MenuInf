package com.codeace.menuinf.workers

import android.os.AsyncTask
import android.util.Log
import com.codeace.menuinf.entity.FoodData

class SyncDatabaseAT(
    private val onInsert: (FoodData) -> Unit,
    private val onUpdate: (FoodData) -> Unit,
    private val onDelete: (FoodData) -> Unit
) : AsyncTask<List<FoodData>, Void, Void>() {
    override fun doInBackground(vararg p: List<FoodData>): Void? {
        val dbList = p[0]
        val fbList = p[1]
        for (fbIndex: Int in fbList.indices) {
            Log.d("Sync", "fb")
            for (dbIndex: Int in dbList.indices) {
                Log.d("Sync", "db")
                if (dbIndex == fbIndex) {
                    Log.d("Sync", "index e $dbIndex : $fbIndex")
                    Log.d("Sync", "index id o $dbIndex : ${fbList[fbIndex].id}")
                    if (dbIndex != fbList[fbIndex].id) {
                        Log.d("Sync", "index id !e $dbIndex : ${fbList[fbIndex].id}")
                        onDelete(dbList[dbIndex])
                        onInsert(fbList[fbIndex])
                    } else if (dbIndex == fbList[fbIndex].id) {
                        Log.d("Sync", "index id e $dbIndex : ${fbList[fbIndex].id}")
                        if (dbList[dbIndex].food_image != fbList[fbIndex].food_image
                            && dbList[dbIndex].food_name != fbList[fbIndex].food_name
                            && dbList[dbIndex].food_category != fbList[fbIndex].food_category
                            && dbList[dbIndex].food_spiciness != fbList[fbIndex].food_spiciness
                            && dbList[dbIndex].food_price != fbList[fbIndex].food_price
                        ) {
                            Log.d("Sync", "Update")
                            onUpdate(fbList[fbIndex])
                        }
                    }
                } else {
                    Log.d("Sync", "index !e $dbIndex : $fbIndex")
                    if (dbIndex > fbIndex) {
                        onDelete(dbList[dbIndex])
                    } else {
                        onInsert(fbList[fbIndex])
                    }
                }
            }
            onInsert(fbList[fbIndex])
        }
        return null
    }
}