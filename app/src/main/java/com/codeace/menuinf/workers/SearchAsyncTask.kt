package com.codeace.menuinf.workers

import android.os.AsyncTask
import android.util.Log
import com.codeace.menuinf.db.FoodDataDao
import com.codeace.menuinf.entity.FoodData
import com.codeace.menuinf.helpers.TAG

class SearchAsyncTask(
    private val dao: FoodDataDao,
    private val type: Int,
    private val list: List<String> = listOf(),
    private val onPostExecuteComplete: (List<FoodData>) -> Unit
) :
    AsyncTask<String, Void, List<FoodData>>() {

    override fun doInBackground(vararg p: String): List<FoodData> {
        return when (type) {
            0 -> dao.searchFoodData(p[0])
            1 -> dao.filterFoodData(p[0].toInt(), p[1].toInt(), list)
            else -> listOf()
        }
    }

    override fun onPostExecute(list: List<FoodData>) {
        super.onPostExecute(list)
        onPostExecuteComplete(list)
        Log.d(TAG, "Filtered list :\t$list")
    }
}