package com.example.recipeapp


import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.Message
import android.util.Log
import android.widget.Toast
import com.example.recipeapp.Interfaces.GetDataService
import com.example.recipeapp.entities.MealResponse
import com.example.recipeapp.entities.MealsEntity
import com.example.recipeapp.retrofitclient.RetrofitClientInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread

class CacheService : Service() {

    var cacheBinder = CacheBinder()



    class CacheBinder : Binder() {

        var hm = HashMap<String, MealsEntity>()

//        fun getSpecificItem(id:String) {
//            // 获取网络或缓存服务获取数据的单例
//
//            val service = RetrofitClientInstance.retrofitInstance!!.create(GetDataService::class.java)
//            val call = service.getSpecificItem(id)
//
//            // 通过id获取数据
//            call.enqueue(object : Callback<MealResponse> {
//                override fun onFailure(call: Call<MealResponse>, t: Throwable) {
//
//
//                }
//
//                override fun onResponse(
//                    call: Call<MealResponse>,
//                    response: Response<MealResponse>
//                ) {
//
//
//                    setCache(id, response.body()!!.mealsEntity[0])
//
//                }
//
//            })
//        }

        // 要么从缓存中获取，要么从网络获取，并缓存
        fun getCache(idMeal: String) : MealsEntity?{
            Log.d("CacheService", "getCache: $idMeal")

            if(hm.contains(idMeal)){
                return hm[idMeal]
            }else{

                return null
            }
        }

        fun setCache(idMeal: String, meal: MealsEntity){
            Log.d("CacheService", "setCache: $idMeal")
            hm[idMeal] = meal
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        return cacheBinder
    }
}