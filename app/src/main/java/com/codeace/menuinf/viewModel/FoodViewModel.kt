package com.codeace.menuinf.viewModel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import com.codeace.menuinf.entity.FoodData
import com.codeace.menuinf.helpers.showMessage
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask

class FoodViewModel(application: Application) : AndroidViewModel(application) {
    private val HOT_STOCK_REF = FirebaseDatabase.getInstance().reference.child("foodData")
    private val foodImageStorage = FirebaseStorage.getInstance().reference

    private var menu: MutableList<FoodData> = mutableListOf()

    internal val selectedCategories = mutableListOf<Int>()
    internal val categoryListItems = mutableSetOf<String>()

    private val liveData = FireBaseLiveData { menu = it.toMutableList() }

    internal var maxPrice: Double = 0.0
    internal var isChanged: Boolean = true

    init {
        liveData.query = HOT_STOCK_REF
        liveData.setListener()
    }

    fun getLiveData(): FireBaseLiveData {
        return liveData
    }

    fun setLiveData(list: List<FoodData>) {
        liveData.value = list
    }

    fun setDefault() {
        liveData.value = menu
    }

    private fun clearItems() {
        categoryListItems.clear()
        maxPrice = 0.0
    }

    fun searchItem(text: String): List<FoodData> {
        return menu.filter { data ->
            data.food_name.contains(text, true)
        }
    }

    fun getCategories(list: List<FoodData>) {
        clearItems()
        list.forEach {
            categoryListItems.add(it.food_category)
            maxPrice = maxOf(maxPrice, it.food_price)
        }
    }

    fun filterByData(minPrice: Double = 0.0, maxPrice: Double = 0.0): List<FoodData> {
        val listItem: MutableList<FoodData> = mutableListOf()

        if (!selectedCategories.isNullOrEmpty()) {
            selectedCategories.forEach { pos ->
                menu.filter { s -> s.food_category == categoryListItems.toList()[pos] && s.food_price in minPrice..maxPrice }
                    .forEach {
                        listItem.add(it)
                    }
            }
        } else {
            menu.filter { s -> s.food_price in minPrice..maxPrice }
                .forEach {
                    listItem.add(it)
                }
        }

        return if (listItem.isNullOrEmpty())
            menu
        else
            listItem
    }

    fun insert(foodData: FoodData, email: String) {
        foodData.id = menu.last().id?.plus(1)
        uploadPicture(foodData, email.plus("_${foodData.id}"))
        isChanged = true
    }

    fun update(foodData: FoodData, email: String) {
        uploadPicture(foodData, email.plus("_${foodData.id}"))
        isChanged = true
    }

    fun delete(foodName: String, id: Int, email: String) {
        foodImageStorage.child("foodImage/$foodName$id.jpg").delete()
            .addOnSuccessListener {
                liveData.delete(email.plus("_$id"))
            }.addOnFailureListener {
                showMessage(getApplication(), it.localizedMessage.toString())
            }
        isChanged = true
    }

    fun deleteAll() {
        isChanged = true
    }

    private fun uploadPicture(data: FoodData, email: String) {
        val imageUri = foodImageStorage.child("foodImage/${data.food_name}${data.id}.jpg")
        if (data.food_image.contains("https://firebasestorage.googleapis.com", true)) {
            liveData.insert(data, email)
        } else {
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
                        liveData.insert(data, email)
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