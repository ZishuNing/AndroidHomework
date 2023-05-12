package com.example.recipeapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.recipeapp.entities.Category
import com.example.recipeapp.entities.CategoryItems
import com.example.recipeapp.entities.MealsItems
import com.example.recipeapp.entities.Recipes

//数据库操作
@Dao
interface RecipeDao {
    //查询所有菜品类别
    @Query("SELECT * FROM categoryitems ORDER BY id DESC")
    suspend fun getAllCategory() :List<CategoryItems>

    //"OnConflictStrategy.REPLACE"表示如果要插入的数据已经存在，则先删除已有的数据，再插入新的数据
    //插入categoryItems
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun  insertCategory(categoryItems: CategoryItems)

    @Query("DELETE FROM categoryitems")
    suspend fun clearDb()


    // 插入Meal
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(mealsItems: MealsItems?)

    @Query("SELECT * FROM MealItems WHERE categoryName =  :categorName ORDER BY id DESC")
    suspend fun getSpecificMealList(categorName:String) :List<MealsItems>
}