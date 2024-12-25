package com.example.mobproject.network

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface RecipeService {
    @GET("categories.php")
    suspend fun getCategories(): Response<RecipeCategoryResponse>
}

object RecipeApi {
    val retrofitService: RecipeService by lazy {
        retrofit.create(RecipeService::class.java)
    }
}