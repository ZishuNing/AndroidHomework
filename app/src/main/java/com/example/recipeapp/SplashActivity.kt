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
    fun getCategories() {
        val service = RetrofitClientInstance.retrofitInstance!!.create(GetDataService::class.java)
        val call = service.getCategoryList()
        call.enqueue(object : Callback<List<Category>> {
            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                Toast.makeText(this@SplashActivity, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
            }


            override fun onResponse(
                call: Call<List<Category>>,
                response: Response<List<Category>>
            ) {
                insertDataIntoRoomDb(response.body())
            }
        })
    }
    fun insertDataIntoRoomDb(category: List<Category>?) {

    }
}