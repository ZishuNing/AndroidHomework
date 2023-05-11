package com.example.recipeapp.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

//数据库菜品表
//Kotlin可以用data class关键字来声明一个数据类，用于封装数据
@Entity(tableName = "Recipes")
data class Recipes(
    @PrimaryKey(autoGenerate = true)//插入新数据时，自动生成唯一主键值
    var id:Int,//菜ID

    @ColumnInfo(name = "dishName")//列名为 dishName
    var dishName:String//菜名
) : Serializable//序列化