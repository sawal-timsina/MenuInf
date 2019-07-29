package com.codeace.menuinf.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.codeace.menuinf.foodData.FoodData
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.ArrayList
import java.util.concurrent.TimeUnit

class ApiServiceFactory {
    private fun providesOkHttpClientBuilder(): OkHttpClient {

        val httpClient = OkHttpClient.Builder()
        return httpClient.readTimeout(1200, TimeUnit.SECONDS)
            .connectTimeout(1200, TimeUnit.SECONDS).build()

    }

    fun providesWebService(): LiveData<List<FoodData>> {
        val data = MutableLiveData<List<FoodData>>()
        var webserviceResponseList: List<FoodData>

        try {
            val retrofit = Retrofit.Builder()
                .baseUrl("")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(providesOkHttpClientBuilder())
                .build()

            //Defining retrofit api service
            val service = retrofit.create(ApiService::class.java)
            //  response = service.makeRequest().execute().body();
            service.makeRequest().enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Log.d("Repository", "Response::::" + response.body()!!)
                    webserviceResponseList = parseJson(response.body())
                    data.value = webserviceResponseList
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d("Repository", "Failed:::")
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return data

    }

    private fun parseJson(response: String?): List<FoodData> {

        val apiResults = ArrayList<FoodData>()

        val jsonArray: JSONArray

        try {
            jsonArray = JSONArray(response)

            for (i in 0 until jsonArray.length()) {
                val jsonInfo: JSONObject = jsonArray.getJSONObject(i)

                val foodModal = FoodData(
                    Integer.parseInt(jsonInfo.getString("id")),
                    jsonInfo.getString("food_image"),
                    jsonInfo.getString("food_name"),
                    jsonInfo.getString("food_category"),
                    jsonInfo.getString("food_spiciness"),
                    (jsonInfo.getString("food_price")).toDouble()
                )
                apiResults.add(foodModal)
            }


        } catch (e: JSONException) {
            e.printStackTrace()
        }

        Log.i(javaClass.simpleName, apiResults.size.toString())
        return apiResults

    }
}