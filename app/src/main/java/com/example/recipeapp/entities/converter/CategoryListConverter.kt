package com.example.recipeapp.entities.converter

import androidx.room.TypeConverter
import com.example.recipeapp.entities.Category
import com.example.recipeapp.entities.CategoryItems
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CategoryListConverter{
    // 将 List<CategoryItems> 转换为JSON
    @TypeConverter
    fun fromCategoryList(category: List<CategoryItems>?):String?{
        // 如category为空，返回空字符串
        if(category==null){
            return ""
        }else{
            //Gson是Google的库，可以实现对象和JSON转换
            val gson=Gson()
            //TypeToken是Gson的一个类，因为Java的泛型在编译时会被擦除，所以Gson无法直接获取泛型类型，要借助TypeToken才可以在运行时期获取泛型类型信息
            //在这里用Typetoken的.type属性才能得知列表里是Category，然后才能正确转换
            val type = object : TypeToken<CategoryItems>(){}.type
            // 使用Gson把category转换为字符串并返回
            return gson.toJson(category,type)
        }
    }
    //将JSON转换为 List<Category>
    @TypeConverter
    fun toCategoryList ( categoryString: String?):List<CategoryItems>?{
        // 如果JSON为空，返回null
        if (categoryString == null){
            return (null)
        }else{
            val gson = Gson()
            val type = object :TypeToken<CategoryItems>(){}.type
            return  gson.fromJson(categoryString,type)
        }
    }
}
