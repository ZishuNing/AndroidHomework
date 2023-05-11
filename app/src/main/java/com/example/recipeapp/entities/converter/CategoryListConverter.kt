package com.example.recipeapp.entities.converter

import androidx.room.TypeConverter
import com.example.recipeapp.entities.Category
import com.example.recipeapp.entities.CategoryItems
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CategoryListConverter{
    @TypeConverter
    fun fromCategoryList(category: List<Category>):String{
        if(category==null){
            return ""
        }else{
            val gson=Gson()
            val type = object : TypeToken<Category>(){

            }.type
            return gson.toJson(category,type)
        }
    }
    @TypeConverter
    fun toCategoryList ( categoryString: String):List<CategoryItems>?{
        if (categoryString == null){
            return (null)
        }else{
            val gson = Gson()
            val type = object :TypeToken<CategoryItems>(){

            }.type
            return  gson.fromJson(categoryString,type)
        }
    }
}
