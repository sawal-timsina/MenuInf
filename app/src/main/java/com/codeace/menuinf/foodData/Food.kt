package com.codeace.menuinf.foodData

import java.io.Serializable

data class Food(val id: Int? = null,
                var image: String,
                var name: String,
                var category: String,
                var spiciness: String,
                var price: Double) : Serializable