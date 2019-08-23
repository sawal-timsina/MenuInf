package com.codeace.menuinf.workers

import android.os.AsyncTask
import com.codeace.menuinf.db.FoodDataDao
import com.codeace.menuinf.entity.FoodData

class DatabaseAsyncTask internal constructor(
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