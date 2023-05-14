package com.example.recipeapp.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.recipeapp.entities.converter.MealListConverter
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

// 菜品
@Entity(tableName = "Meal")
@TypeConverters(MealListConverter::class) // 声明需要使用MealListConverter进行类型转换，即在拿出来时，从JSON还原为List<MealsItems>，官方手册上面说，此注解需要写在这里，才能build成功
class Meal(

    @PrimaryKey(autoGenerate = true)
    var id:Int,

    @ColumnInfo(name = "meals")
    @Expose
    @SerializedName("meals")
    var mealsItem: List<MealsItems>? = null

)

