package com.codeace.menuinf.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.codeace.menuinf.foodData.FoodData
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiServiceFactory {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://5d4027cec516a90014e89625.mockapi.io/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(providesOkHttpClientBuilder())
        .build()
    private fun providesOkHttpClientBuilder(): OkHttpClient {

        val httpClient = OkHttpClient.Builder()
        return httpClient.readTimeout(1200, TimeUnit.SECONDS)
            .connectTimeout(1200, TimeUnit.SECONDS).build()

    }

    private fun initRetrofit(): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    fun insertFoodData(foodData: FoodData) {
        try {
            val service = initRetrofit()

            service.insertFoodData(foodData).enqueue(object : Callback<FoodData> {
                override fun onResponse(call: Call<FoodData>, response: Response<FoodData>) {
                    if (response.isSuccessful) {
                        Log.d("Repository", "Success")
                    } else {
                        Log.d("Repository", "Failed to add item")
                    }
                }

                override fun onFailure(call: Call<FoodData>, t: Throwable) {
                    Log.d("Repository", "Failed to add item")
                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getFoodData(): LiveData<List<FoodData>> {
        val data = MutableLiveData<List<FoodData>>()

        try {
            val service = initRetrofit()

            service.requestData().enqueue(object : Callback<List<FoodData>> {
                override fun onResponse(call: Call<List<FoodData>>, response: Response<List<FoodData>>) {
                    Log.d("Repository", "Response::::" + response.body().toString())
                    data.value = response.body()!!
                }

                override fun onFailure(call: Call<List<FoodData>>, t: Throwable) {
                    Log.d("Repository", "Failed:::")
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return data
    }

    private fun parseJson(response: FoodData): RequestBody {

        val json = JSONObject()
        json.put("id", response.id)
        json.put("food_image", response.image)
        json.put("food_name",response.name)
        json.put("food_category",response.category)
        json.put("food_spiciness",response.spiciness)
        json.put("food_price",response.price)

        return RequestBody.create(MediaType.parse("application/json"), json.toString())

    }
}