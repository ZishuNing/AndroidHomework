package com.example.recipeapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.widget.Button
import com.example.recipeapp.Interfaces.GetDataService
import com.example.recipeapp.entities.Category
import com.example.recipeapp.retrofitclient.RetrofitClientInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//加载界面
class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val btnGetStarted=findViewById<Button>(R.id.btnGetStarted)
        //点击btnGetStarted转到HomeActivity
        btnGetStarted.setOnClickListener {
            val intent=Intent(this,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    //使用Retrofit获取类别列表
    fun getCategories() {
        //创建网络请求接口的实例
        //create()是retrofit内置的，接收一个Class<T>参数
        //然后Retrofit会自动使用反射来生成一个实现了该接口的代理对象，对象里会有格式转换、HTTP请求之类的，我们只管处理请求就行
        val service = RetrofitClientInstance.retrofitInstance!!.create(GetDataService::class.java)
        //告诉retrofit具体请求
        val call = service.getCategoryList()
        //异步请求，enqueue把请求添加到请求队列中，然后在后台线程中执行该请求
        call.enqueue(object : Callback<List<Category>> {
            //在请求完成后回调onResponse()或onFailure()
            //请求失败
            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                Toast.makeText(this@SplashActivity, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
            }
            //请求成功，把数据插入到数据库中
            override fun onResponse(
                call: Call<List<Category>>,
                response: Response<List<Category>>
            ) {
                insertDataIntoRoomDb(response.body())
            }
        })
    }
    //把数据插入到数据库中
    fun insertDataIntoRoomDb(category: List<Category>?) {

    }
}