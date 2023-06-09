package com.example.recipeapp.retrofitclient

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//单例类
class RetrofitClientInstance {
    companion object{
        //数据来自https://www.themealdb.com/
        //调用https://www.themealdb.com/api/json/v1/1/categories.php
        val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"
        private var retrofit: Retrofit? = null
        val retrofitInstance: Retrofit?
            get() {
                if (retrofit == null) {
                    retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                }
                return retrofit
            }
    }
}