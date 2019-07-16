package com.codeace.menuinf

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_items")
data class FoodData(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Int,
    @ColumnInfo(name = "food_image") var image: Uri,
    @ColumnInfo(name = "food_name") var name: String,
    @ColumnInfo(name = "food_category") var category: String,
    @ColumnInfo(name = "food_spiciness") var spiciness: String,
    @ColumnInfo(name = "food_price") var price: Double
)