package com.example.recipeapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.recipeapp.dao.RecipeDao
import com.example.recipeapp.entities.*

//"exportSchema = false"指不导出数据库JSON结构文件
@Database(entities = [Recipes::class],version=1, exportSchema = false)
abstract class RecipeDatabase:RoomDatabase() {
    //单例环境，避免创建多个数据库实例
    companion object{
        //?为可空类型修饰符，表示 recipesDatabase 可以为 null。
        var recipesDatabase:RecipeDatabase? = null

        //为了避免有多个程序同时调用recipesDatabase对象所以加上了@Synchronized同步锁，同一时间只能有一个线程可以访问该方法
        @Synchronized
        fun getDatabase(context: Context): RecipeDatabase{//返回RecipeDatabase实例
            if (recipesDatabase == null){
                recipesDatabase = Room.databaseBuilder(
                    context,//上下文对象
                    RecipeDatabase::class.java,//数据库抽象类
                    "recipe.db"//数据库名称
                ).build()
            }
            //第一次创建之后数据库一定存在，但编译器并不知道，会报错，需要用!!告诉编译器变量一定不为空
            return recipesDatabase!!
        }
    }
    //用于获取与 Recipe 实体相关的DAO
    abstract fun recipeDao(): RecipeDao
}