package com.codeace.menuinf.foodData

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Food(val id: Int? = null,
                var food_image: String = "",
                var food_name: String = "",
                var food_category: String = "",
                var food_spiciness: String = "",
                var food_price: Double = 0.0)
