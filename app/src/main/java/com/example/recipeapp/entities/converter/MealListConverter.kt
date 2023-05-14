package com.example.recipeapp.entities.converter

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.recipeapp.entities.MealsItems
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MealListConverter {

    // 将 List<MealsItems> 转换为JSON
    @TypeConverter
    fun fromCategoryList(category: List<MealsItems>?):String?{
        if (category == null){
            return (null)
        }else{
            val gson = Gson()
            val type = object : TypeToken<MealsItems>(){

            }.type
            return gson.toJson(category,type)
        }
    }


    //将JSON转换为 List<MealsItems>
    @TypeConverter
    fun toCategoryList ( categoryString: String?):List<MealsItems>?{
        if (categoryString == null){
            return (null)
        }else{
            val gson = Gson()
            val type = object :TypeToken<MealsItems>(){

            }.type
            return  gson.fromJson(categoryString,type)
        }
    }
}
