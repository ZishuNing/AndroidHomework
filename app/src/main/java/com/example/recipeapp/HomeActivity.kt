package com.example.recipeapp

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.adapter.FavouriteAdapter
import com.example.recipeapp.adapter.MainCategoryAdapter
import com.example.recipeapp.adapter.SubCategoryAdapter
import com.example.recipeapp.database.RecipeDatabase
import com.example.recipeapp.entities.CategoryItems
import com.example.recipeapp.entities.MealsEntity
import com.example.recipeapp.entities.MealsItems
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

//主界面
class HomeActivity : BaseActivity() {

    //主次菜单菜品数据
    var arrMainCategory = ArrayList<CategoryItems>()
    var arrSubCategory = ArrayList<MealsItems>()

    //主次菜单RecyclerView的Adapter
    var mainCategoryAdapter = MainCategoryAdapter()
    var subCategoryAdapter = SubCategoryAdapter()
    var favCategoryAdapter = FavouriteAdapter()

    var favs : ArrayList<MealsItems> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        getDataFromDb()

        mainCategoryAdapter.setClickListener(onCLicked)
        subCategoryAdapter.setClickListener(onCLickedSubItem)
        startService(Intent(this@HomeActivity,CacheService::class.java)) // 启动服务
        // 绑定服务，从服务里面获取数据，或从网络里面获取数据并缓存起来
        val intent = Intent(this, CacheService::class.java)
        bindService(intent, connection, BIND_AUTO_CREATE)
        val btnProfile = findViewById<Button>(R.id.btn_profile)
        btnProfile.setOnClickListener {
            // 向控制台输出日志
            Log.d("ahduabdj", "Button clicked")
            // 显示 Toast 消息
            Toast.makeText(this, "Button clicked", Toast.LENGTH_SHORT).show()
            // 启动 ProfileActivity
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)// 解绑，因为服务是在HomeActivity里面启动的，所以在这里解绑，不会使它消失，除非调用stopService
    }


    override fun onResume() {
        super.onResume()
        favs.clear()
        // 当从其他界面返回的时候，重新获取favourite
        if(connection.binder != null){
            getDataFromProvider()
            favCategoryAdapter.setClickListener(onCLickedFavItem)
        }

    }

    private fun getDataFromProvider() {
        // 获取所有favourite
        val fav_cursor = contentResolver.query(Uri.parse("content://com.example.recipeapp.provider/favourite") ,null,null,null,null)


        val meals = ArrayList<String>()


        fav_cursor?.moveToFirst()
        var i = 0
        // 对每一个id调用
        while(fav_cursor?.isAfterLast != true){
            if (fav_cursor != null) {
                meals.add(fav_cursor.getString(0))
            }
            i++
            if (fav_cursor != null) {
                fav_cursor.moveToNext()
            }
        }
        if (fav_cursor != null) {
            fav_cursor.close()
        }

        getItems(meals)



        val rv_fav_category = findViewById<RecyclerView>(R.id.rv_fav_category)

        rv_fav_category.layoutManager = LinearLayoutManager(this@HomeActivity,LinearLayoutManager.HORIZONTAL,false)
        rv_fav_category.adapter = favCategoryAdapter

    }

    // 使用Service获取数据
    fun getItems(meals:ArrayList<String>) {


        for(i in meals.indices){
            val meal = connection.binder!!.getCache(meals[i], object : CacheService.CacheCallback{
                override fun onCacheGet(meal: MealsEntity) {
                    // 小型meal
                    show(meal)

                }
            })

        }

    }



    // 函数指针，点击之后执行onClicked函数
    private val onCLicked  = object : MainCategoryAdapter.OnItemClickListener{
        override fun onClicked(categoryName: String) {
            getMealDataFromDb(categoryName)
        }
    }

    // 函数指针，点击之后执行onClicked函数, 传送到DetailActivity
    private val onCLickedSubItem  = object : SubCategoryAdapter.OnItemClickListener{
        override fun onClicked(id: String) {
            var intent = Intent(this@HomeActivity,DetailActivity::class.java)
            intent.putExtra("id",id)
            startActivity(intent)
        }
    }

    // 函数指针，点击之后执行onClicked函数, 传送到DetailActivity
    private val onCLickedFavItem  = object : FavouriteAdapter.OnItemClickListener{
        override fun onClicked(id: String) {
            var intent = Intent(this@HomeActivity,DetailActivity::class.java)
            intent.putExtra("id",id)
            startActivity(intent)
        }
    }


    fun show(enti: MealsEntity){

        val meal = MealsItems(
            favs.size,
            enti.idmeal,
            enti.strcategory,
            enti.strmeal,
            enti.strmealthumb
        )
        favs.add(meal)
        favCategoryAdapter.setData(favs)
        val rv_fav_category = findViewById<RecyclerView>(R.id.rv_fav_category)

        rv_fav_category.layoutManager = LinearLayoutManager(this@HomeActivity,LinearLayoutManager.HORIZONTAL,false)
        rv_fav_category.adapter = favCategoryAdapter
    }



    private val connection = object : ServiceConnection {

        var binder : CacheService.CacheBinder ?= null
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binder = service as CacheService.CacheBinder
            getDataFromProvider()
            favCategoryAdapter.setClickListener(onCLickedFavItem)
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("HomeActivity", "onServiceDisconnected: ")
        }
    }






    private fun getDataFromDb(){
        launch {
            this.let {
                // 获得所有的Category，并展示
                var cat = RecipeDatabase.getDatabase(this@HomeActivity).recipeDao().getAllCategory()
                arrMainCategory = cat as ArrayList<CategoryItems>
                arrMainCategory.reverse()

                getMealDataFromDb(arrMainCategory[0].strcategory)
                mainCategoryAdapter.setData(arrMainCategory)
                val rvMainCategory = findViewById<RecyclerView>(R.id.rv_main_category)
                rvMainCategory.layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)//HORIZONTAL表示子项将水平排列，false表示从左向右排列
                rvMainCategory.adapter = mainCategoryAdapter
            }
        }
    }

    private fun getMealDataFromDb(categoryName:String){
        val tvCategory = findViewById<TextView>(R.id.tvCategory)

        tvCategory.text = "$categoryName Category"
        launch {
            this.let {
                // 获得所有的MealData，并展示
                val cat = RecipeDatabase.getDatabase(this@HomeActivity).recipeDao().getSpecificMealList(categoryName)
                arrSubCategory = cat as ArrayList<MealsItems>
                subCategoryAdapter.setData(arrSubCategory)

                val rv_sub_category = findViewById<RecyclerView>(R.id.rv_sub_category)

                rv_sub_category.layoutManager = LinearLayoutManager(this@HomeActivity,LinearLayoutManager.HORIZONTAL,false)
                rv_sub_category.adapter = subCategoryAdapter

                while(connection.binder == null){
                    yield() // 这里就可以yield了，因为是在子协程里面
                }

                val ids = ArrayList<String>()
                for(i in arrSubCategory.indices){
                    ids.add(arrSubCategory[i].idMeal)
                }

                connection.binder!!.makeCache(ids)
            }


        }
    }
}