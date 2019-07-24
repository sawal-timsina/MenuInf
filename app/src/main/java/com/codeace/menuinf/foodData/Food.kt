package com.codeace.menuinf.foodData

data class Food(val id: Int? = null,
                var image: String,
                var name: String,
                var category: String,
                var spiciness: String,
                var price: Double)