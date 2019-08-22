package com.codeace.menuinf.viewModel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.codeace.menuinf.db.FoodRepository
import com.codeace.menuinf.entity.FoodData
import com.codeace.menuinf.helpers.showMessage
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask

class FoodViewModel(application: Application) : AndroidViewModel(application) {
    private val foodImageStorage = FirebaseStorage.getInstance().reference

    private val repository = FoodRepository(application)

    internal val selectedCategories = mutableListOf<Int>()

    internal val categoryListItems = mutableSetOf<String>()

    internal var maxPrice: Double = 0.0

    fun getIsDataChanged(): Boolean {
        return repository.isDataChanged
    }

    fun setIsDataChanged(boolean: Boolean) {
        repository.isDataChanged = boolean
    }

    fun getLiveData(): LiveData<List<FoodData>> {
        return repository.getAllFoodData()
    }

    fun setLiveData(list: List<FoodData>) {
        repository.getAllFoodData().value = list
        Log.d(com.codeace.menuinf.helpers.TAG, "New Live Data")
    }

    fun setDefault() {
        repository.getAllFoodData().value = repository.menu
        Log.d(com.codeace.menuinf.helpers.TAG, "Default Live Data")
    }

    private fun clearItems() {
        categoryListItems.clear()
        maxPrice = 0.0
    }

    fun searchItem(text: String): List<FoodData> {
        return repository.menu.filter { data ->
            data.food_name.contains(text, true)
        }
    }

    fun getCategories() {
        clearItems()
        repository.menu.forEach {
            categoryListItems.add(it.food_category)
            maxPrice = maxOf(maxPrice, it.food_price)
        }
        Log.d(com.codeace.menuinf.helpers.TAG, "Refreshing Categories")
    }

    fun filterByData(minPrice: Double = 0.0, maxPrice: Double = 0.0): List<FoodData> {
        val listItem: MutableList<FoodData> = mutableListOf()
        if (!selectedCategories.isNullOrEmpty()) {
            selectedCategories.forEach { pos ->
                repository.menu.filter { s -> s.food_category == categoryListItems.toList()[pos] && s.food_price in minPrice..maxPrice }
                    .forEach {
                        listItem.add(it)
                    }
            }
        } else {
            repository.menu.filter { s -> s.food_price in minPrice..maxPrice }
                .forEach {
                    listItem.add(it)
                }
        }
        return listItem
    }

    fun insert(foodData: FoodData, email: String) {
        foodData.id = if (repository.menu.isEmpty()) 0 else repository.menu.last().id?.plus(1)
        uploadPicture(foodData, email.plus("_${foodData.id}"))
        repository.isDataChanged = true
    }

    fun update(foodData: FoodData, email: String) {
        uploadPicture(foodData, email.plus("_${foodData.id}"))
        repository.isDataChanged = true
    }

    fun delete(foodName: String, id: Int, email: String) {
        foodImageStorage.child("foodImage/$foodName$id.jpg").delete()
            .addOnSuccessListener {
                repository.delete(email.plus("_$id"))
                showMessage(getApplication(), "Data Deleted Successfully")
            }.addOnFailureListener {
                showMessage(getApplication(), it.localizedMessage.toString())
            }
        repository.isDataChanged = true
    }

    fun deleteAll() {
    }

    private fun uploadPicture(data: FoodData, email: String) {
        val imageUri = foodImageStorage.child("foodImage/${data.food_name}${data.id}.jpg")
        if (data.food_image.contains("https://firebasestorage.googleapis.com", true)) {
            repository.insert(data, email)
            showMessage(this.getApplication(), "Data Updated Successfully")
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
                        repository.insert(data, email)
                        showMessage(this.getApplication(), "Data Inserted Successfully")
                    } else {
                        showMessage(
                            getApplication(),
                            taskSnapshot.exception?.localizedMessage.toString()
                        )
                    }
                }
        }
    }
}