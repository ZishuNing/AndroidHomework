package com.example.recipeapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.recipeapp.entities.Category
import com.example.recipeapp.entities.Recipes

//数据库操作
@Dao
interface RecipeDao {
    //查询所有菜品类别
    @get:Query("SELECT * FROM category ORDER BY id DESC")
    val getAllCategory:List<Category>

    //"OnConflictStrategy.REPLACE"表示如果要插入的数据已经存在，则先删除已有的数据，再插入新的数据
    //插入菜品类别
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun  insertCategory(category: Category)
}