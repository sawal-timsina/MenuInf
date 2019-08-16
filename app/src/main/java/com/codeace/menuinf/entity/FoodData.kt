package com.codeace.menuinf.entity

import com.google.firebase.database.IgnoreExtraProperties
import org.apache.commons.lang3.builder.ToStringBuilder
import java.io.Serializable

@IgnoreExtraProperties
data class FoodData(
    var id: Int? = null,
    var food_image: String = "",
    var food_name: String = "",
    var food_category: String = "",
    var food_spiciness: String = "",
    var food_price: Double = 0.0
) : Serializable {
    override fun toString(): String {
        return ToStringBuilder(this).append("id", id).append("foodImage", food_image)
            .append("foodName", food_name).append("foodCategory", food_category)
            .append("foodSpiciness", food_spiciness).append("foodPrice", food_price).toString()
    }
}