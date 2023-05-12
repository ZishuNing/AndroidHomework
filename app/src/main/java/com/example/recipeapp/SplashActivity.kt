package com.example.recipeapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.Button
import android.widget.ProgressBar
import com.example.recipeapp.Interfaces.GetDataService
import com.example.recipeapp.database.RecipeDatabase
import com.example.recipeapp.entities.Category
import com.example.recipeapp.entities.Meal
import com.example.recipeapp.entities.MealsItems
import com.example.recipeapp.retrofitclient.RetrofitClientInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//加载界面
class SplashActivity : BaseActivity() ,EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks{

    private var READ_STORAGE_PERM = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

//        readStorageTask()


        clearDatabase()
        getCategories()


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
        call.enqueue(object : Callback<Category> {
            //在请求完成后回调onResponse()或onFailure()
            //请求失败
            override fun onFailure(call: Call<Category>, t: Throwable) {
                Log.d("MainActivity", "onFailure: " + t.message)
                Toast.makeText(this@SplashActivity, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
            }
            //请求成功，把数据插入到数据库中
            override fun onResponse(
                call: Call<Category>,
                response: Response<Category>
            ) {

                for(arr in response.body()!!.categorieitems!!){
                    getMeal(arr.strcategory)
                }



                insertDataIntoRoomDb( response.body())
            }
        })
    }


    // 获取Meal
    fun getMeal(categoryName: String) {
        val service = RetrofitClientInstance.retrofitInstance!!.create(GetDataService::class.java)
        val call = service.getMealList(categoryName)
        call.enqueue(object : Callback<Meal> {
            override fun onFailure(call: Call<Meal>, t: Throwable) {

                val loader = findViewById<ProgressBar>(R.id.loader)

                loader.visibility = View.INVISIBLE
                Toast.makeText(this@SplashActivity, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onResponse(
                call: Call<Meal>,
                response: Response<Meal>
            ) {

                insertMealDataIntoRoomDb(categoryName, response.body())
            }

        })
    }

    fun insertMealDataIntoRoomDb(categoryName: String, meal: Meal?) {

        launch {
            this.let {


                for (arr in meal!!.mealsItem!!) {
                    var mealItemModel = MealsItems(
                        arr.id,
                        arr.idMeal,
                        categoryName,
                        arr.strMeal,
                        arr.strMealThumb
                    )
                    RecipeDatabase.getDatabase(this@SplashActivity)
                        .recipeDao().insertMeal(mealItemModel)
                    Log.d("mealData", arr.toString())
                }

                val btnGetStarted = findViewById<Button>(R.id.btnGetStarted)

                btnGetStarted.visibility = View.VISIBLE
            }
        }


    }






    //把数据item插入到数据库中, 数据来自getCategories()
    fun insertDataIntoRoomDb(category: Category?): Boolean {

        CoroutineScope(this.coroutineContext).launch {// launch 会新建一个协程并立即返回
            this.let{

                // 插入数据
                for(arr in category!!.categorieitems!!){
                    RecipeDatabase.getDatabase(this@SplashActivity).recipeDao().insertCategory(arr)
                }

                var btnGetStarted = findViewById<Button>(R.id.btnGetStarted)
                btnGetStarted.visibility = Button.VISIBLE

            }
        }

        return true

    }

    fun clearDatabase(){
        launch {
            this.let {
                RecipeDatabase.getDatabase(this@SplashActivity).recipeDao().clearDb()

            }
        }
    }

    private  fun hasReadStoragePermission(): Boolean{
        return EasyPermissions.hasPermissions(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }


    private fun readStorageTask(){
        if(hasReadStoragePermission()){
            clearDatabase()
            getCategories()


        }else{
            EasyPermissions.requestPermissions(
                this,
                "This app need access to your storage so as to download images",
                READ_STORAGE_PERM,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults,
            this
        )
    }







    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        clearDatabase()
        getCategories()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Log.d("MainActivity","Permission has been denied")
        if(EasyPermissions.somePermissionPermanentlyDenied(this, perms)){
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onRationaleAccepted(requestCode: Int) {

    }

    override fun onRationaleDenied(requestCode: Int) {

    }
}