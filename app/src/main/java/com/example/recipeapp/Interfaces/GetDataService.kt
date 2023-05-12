package com.example.recipeapp.Interfaces

import com.example.recipeapp.entities.Category
import com.example.recipeapp.entities.Meal
import retrofit2.http.GET
import retrofit2.http.Query

//网络请求接口
interface GetDataService {
    //GET请求categories.php API接口，接口返回Category类型列表
    //www.themealdb.com/api/json/v1/1/categories.php
    //函数返回retrofit2.Call对象，可用于调用API接口获取返回的数据
    //Call是Retrofit库的泛型接口，表示一个HTTP请求
    @GET("categories.php")
    fun getCategoryList(): retrofit2.Call<Category>

    @GET("filter.php")
    fun getMealList(@Query("c") category: String): retrofit2.Call<Meal>
}