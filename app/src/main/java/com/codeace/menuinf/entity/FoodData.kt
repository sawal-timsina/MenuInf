package com.codeace.menuinf.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.apache.commons.lang3.builder.ToStringBuilder
import java.io.Serializable

@Entity(tableName = "food_items")
data class FoodData(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,

    @ColumnInfo(name = "food_image")
    var food_image: String = "",

    @ColumnInfo(name = "food_name")
    var food_name: String = "",

    @ColumnInfo(name = "food_category")
    var food_category: String = "",

    @ColumnInfo(name = "food_spiciness")
    var food_spiciness: String = "",

    @ColumnInfo(name = "food_price")
    var food_price: Double = 0.0
) : Serializable {
    override fun toString(): String {
        return ToStringBuilder(this).append("id", id).append("foodImage", food_image)
            .append("foodName", food_name).append("foodCategory", food_category)
            .append("foodSpiciness", food_spiciness).append("foodPrice", food_price).toString()
    }
}