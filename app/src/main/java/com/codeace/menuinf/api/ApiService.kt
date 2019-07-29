package com.codeace.menuinf.api

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("posts")
    fun makeRequest(): Call<String>
}