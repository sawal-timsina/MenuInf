package com.codeace.menuinf

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.item_details.*


class ItemDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_details)

        val foodData: FoodData = intent.extras!!.get("extra_object") as FoodData
        Adapter.setImage(this, foodData.image, foodImage)
        itemName_.text = foodData.name
        itemCategory_.text = foodData.category
        itemSpiciness_.text = foodData.spiciness
        itemPrice_.text = foodData.price.toString()
    }
}