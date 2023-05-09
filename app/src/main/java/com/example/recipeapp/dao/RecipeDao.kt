package com.example.recipeapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.recipeapp.entities.Recipes

@Dao
interface RecipeDao {
    //get关键字用于在属性上使用注解,Query是Room数据库框架提供的注解，由于该注解是用于属性而不是方法，因此需要使用@get来指示该注解是用于属性的
    @get:Query("SELECT * FROM recipes ORDER BY id DESC")
    val allRecipes:List<Recipes>
    //"OnConflictStrategy.REPLACE"表示如果要插入的数据已经存在，则先删除已有的数据，再插入新的数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun  insertRecipe(recipes: Recipes)
}