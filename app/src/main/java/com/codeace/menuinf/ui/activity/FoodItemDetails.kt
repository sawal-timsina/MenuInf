package com.codeace.menuinf.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codeace.menuinf.R
import com.codeace.menuinf.foodData.FoodData
import com.codeace.menuinf.helpers.setImage
import kotlinx.android.synthetic.main.item_details.*


class FoodItemDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_details)

        val foodData: FoodData = intent.extras!!.get("extra_object") as FoodData
        setImage(this, foodData.food_image, foodImage)
        itemName_.text = foodData.food_name
        itemCategory_.text = foodData.food_category
        itemSpiciness_.text = foodData.food_spiciness
        itemPrice_.text = foodData.food_price.toString()
    }
}