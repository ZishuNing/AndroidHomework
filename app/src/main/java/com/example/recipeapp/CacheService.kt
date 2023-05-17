package com.example.recipeapp


import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.recipeapp.Interfaces.GetDataService
import com.example.recipeapp.entities.MealResponse
import com.example.recipeapp.entities.MealsEntity
import com.example.recipeapp.retrofitclient.RetrofitClientInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Deque
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor

class Downloader(val id :String, val hm: HashMap<String, MealsEntity>) : Runnable{


    override fun run() {
        val service = RetrofitClientInstance.retrofitInstance!!.create(GetDataService::class.java)
        val call = service.getSpecificItem(id)

        // 通过id获取数据
        call.enqueue(object : Callback<MealResponse> {
            override fun onFailure(call: Call<MealResponse>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<MealResponse>,
                response: Response<MealResponse>
            ) {


                synchronized(hm){
                    hm[id] = response.body()!!.mealsEntity[0]
                }

            }

        })
    }



}


class CacheService : Service() {

    var cacheBinder = CacheBinder()




    interface CacheCallback {
        fun onCacheGet(meal: MealsEntity)
    }

    class CacheBinder : Binder() {

        var hm = HashMap<String, MealsEntity>()

        private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
        var theradPool : ExecutorService?= null

        fun getSpecificItem(id:String, fn: CacheCallback?) {
            // 获取网络或缓存服务获取数据的单例

            val service = RetrofitClientInstance.retrofitInstance!!.create(GetDataService::class.java)
            val call = service.getSpecificItem(id)

            // 通过id获取数据
            call.enqueue(object : Callback<MealResponse> {
                override fun onFailure(call: Call<MealResponse>, t: Throwable) {


                }

                override fun onResponse(
                    call: Call<MealResponse>,
                    response: Response<MealResponse>
                ) {

                    // 因为是异步的，所以要在这里调用回调函数，才不会出现空指针异常
                    setCache(id, response.body()!!.mealsEntity[0])
                    fn?.onCacheGet(response.body()!!.mealsEntity[0])

                }

            })
        }




        // 要么从缓存中获取，要么从网络获取，并缓存，将内容作为参数调用回调函数
        fun getCache(idMeal: String, fn: CacheService.CacheCallback?) : MealsEntity?{
            Log.d("CacheService", "getCache: $idMeal")
            synchronized(hm){
                if(hm.contains(idMeal)){
                    fn?.onCacheGet(hm[idMeal]!!)
                    return hm[idMeal]
                }else{
                    getSpecificItem(idMeal, fn)
                    return null
                }
            }
        }

        fun setCache(idMeal: String, meal: MealsEntity){
            synchronized(hm){
                Log.d("CacheService", "setCache: $idMeal")
                hm[idMeal] = meal
            }

        }

        fun makeCache(idMeals: List<String>){
            if(theradPool == null){
                theradPool = Executors.newFixedThreadPool(CPU_COUNT)
            }else{
                theradPool!!.shutdown()// 先关闭线程池
                theradPool = Executors.newFixedThreadPool(CPU_COUNT)
            }

            for(idMeal in idMeals){
                theradPool!!.execute(Downloader(idMeal, hm))
            }

        }
    }



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d("CacheService", "onBind: ")
        return cacheBinder
    }
}