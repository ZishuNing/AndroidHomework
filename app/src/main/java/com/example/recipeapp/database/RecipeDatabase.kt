package com.example.recipeapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.recipeapp.dao.RecipeDao
import com.example.recipeapp.entities.*
import com.example.recipeapp.entities.converter.CategoryListConverter

//"exportSchema = false"指不导出数据库JSON结构文件
@Database(entities = [Recipes::class,CategoryItems::class,Category::class,Meal::class, MealsItems::class],version=1, exportSchema = false)
abstract class RecipeDatabase:RoomDatabase() {
    // 声明需要使用CategoryListConverter进行类型转换，即在存储时，把List<CategoryItems>转换成JSON
    @TypeConverters(CategoryListConverter::class)
    //单例环境，避免创建多个数据库实例
    companion object{
        //?为可空类型修饰符，表示 recipesDatabase 可以为 null。
        var recipesDatabase:RecipeDatabase? = null

        //为了避免有多个程序同时调用recipesDatabase对象所以加上了@Synchronized同步锁，同一时间只能有一个线程可以访问该方法
        @Synchronized
        fun getDatabase(context: Context): RecipeDatabase{//返回RecipeDatabase实例
            if (recipesDatabase == null){//如果数据库为空则创建数据库
                recipesDatabase = Room.databaseBuilder(
                    context,
                    RecipeDatabase::class.java,//数据库抽象类
                    "recipe.db"//数据库名称
                ).allowMainThreadQueries().build() // 允许主线程访问数据库，因为这个app只有协程没有多线程，不允许的话用不了
            }
            //第一次创建之后数据库一定存在，但编译器并不知道，会报错，需要用!!告诉编译器变量一定不为空
            return recipesDatabase!!
        }
    }
    //用于获取与 Recipe 实体相关的DAO
    abstract fun recipeDao(): RecipeDao
}