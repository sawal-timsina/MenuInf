package com.codeace.menuinf.foodData

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.apache.commons.lang3.builder.ToStringBuilder
import java.io.Serializable

@Entity(tableName = "food_items")
data class FoodData(
    @SerializedName("id")
    @Expose
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    @SerializedName("food_image")
    @Expose
    @ColumnInfo(name = "food_image")
    var image: String,

    @SerializedName("food_name")
    @Expose
    @ColumnInfo(name = "food_name")
    var name: String,

    @SerializedName("food_category")
    @Expose
    @ColumnInfo(name = "food_category")
    var category: String,

    @SerializedName("food_spiciness")
    @Expose
    @ColumnInfo(name = "food_spiciness")
    var spiciness: String,

    @SerializedName("food_price")
    @Expose
    @ColumnInfo(name = "food_price")
    var price: Double
) : Serializable {
    override fun toString(): String {
        return ToStringBuilder(this).append("id", id).append("foodImage", image)
            .append("foodName", name).append("foodCategory", category)
            .append("foodSpiciness", spiciness).append("foodPrice", price).toString()
    }
}