package com.example.recipeapp.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "Recipes")
data class Recipes(
    @PrimaryKey(autoGenerate = true)//插入新数据时，自动生成唯一主键值
    var id:Int,

    @ColumnInfo(name = "dishName")//列名为 dishName
    var dishName:String
) : Serializable//序列化