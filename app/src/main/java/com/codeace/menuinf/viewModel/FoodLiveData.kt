package com.codeace.menuinf.viewModel

import androidx.lifecycle.LiveData
import com.codeace.menuinf.entity.FoodData

class FoodLiveData(private val func: (FoodData) -> Unit) : LiveData<List<FoodData>>() {
//    private val LOG_TAG = "FirebaseQueryLiveData"
//    private val listener = FoodValueEventListener()
}