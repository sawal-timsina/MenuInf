package com.codeace.menuinf

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "food_items")
data class FoodData(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "food_image") var image: String,
    @ColumnInfo(name = "food_name") var name: String,
    @ColumnInfo(name = "food_category") var category: String,
    @ColumnInfo(name = "food_spiciness") var spiciness: String,
    @ColumnInfo(name = "food_price") var price: Double
) : Serializable