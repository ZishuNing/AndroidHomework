package com.example.recipeapp.Interfaces

import com.example.recipeapp.entities.Category
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GetDataService {
    @GET("categories.php")
    fun getCategoryList(): retrofit2.Call<List<Category>>

}