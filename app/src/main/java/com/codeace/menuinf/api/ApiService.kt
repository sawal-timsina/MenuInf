package com.codeace.menuinf.api

import com.codeace.menuinf.foodData.FoodData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("foodData")
    fun requestData(): Call<List<FoodData>>

    @POST("foodData")
    fun insertFoodData(@Body foodData: FoodData): Call<FoodData>
}