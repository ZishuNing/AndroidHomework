package com.example.recipeapp

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.Interfaces.GetDataService
import com.example.recipeapp.adapter.FavouriteAdapter
import com.example.recipeapp.adapter.MainCategoryAdapter
import com.example.recipeapp.adapter.SubCategoryAdapter
import com.example.recipeapp.database.RecipeDatabase
import com.example.recipeapp.entities.*
import com.example.recipeapp.retrofitclient.RetrofitClientInstance
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        getDataFromProvider()

        mainCategoryAdapter.setClickListener(onCLicked)
        subCategoryAdapter.setClickListener(onCLickedSubItem)
        favCategoryAdapter.setClickListener(onCLickedFavItem)


    }


    private fun getDataFromProvider() {
        // 获取所有favourite
        val fav_cursor = contentResolver.query(Uri.parse("content://com.example.recipeapp.provider/favourite") ,null,null,null,null)


        var meals = ArrayList<String>()


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

        getSpecificItem(meals)



        val rv_fav_category = findViewById<RecyclerView>(R.id.rv_fav_category)

        rv_fav_category.layoutManager = LinearLayoutManager(this@HomeActivity,LinearLayoutManager.HORIZONTAL,false)
        rv_fav_category.adapter = favCategoryAdapter

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



     fun getSpecificItem(meals:ArrayList<String>) {
        // 获取网络获取数据的MealsEntity
        val service = RetrofitClientInstance.retrofitInstance!!.create(GetDataService::class.java)

         for(i in meals.indices){
             val call = service.getSpecificItem(meals[i])
             call.enqueue(object : Callback<MealResponse> {

                 override fun onFailure(call: Call<MealResponse>, t: Throwable) {

                     Toast.makeText(this@HomeActivity, "Something went wrong" + t.message, Toast.LENGTH_SHORT )
                         .show()
                 }

                 override fun onResponse(
                     call: Call<MealResponse>,
                     response: Response<MealResponse>
                 ) {
                    // 只能这样做了，要想获取图片，网络获取到一个就加入一个
                     val enti = response.body()!!.mealsEntity[0]
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

             })
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
                var cat = RecipeDatabase.getDatabase(this@HomeActivity).recipeDao().getSpecificMealList(categoryName)
                arrSubCategory = cat as ArrayList<MealsItems>
                subCategoryAdapter.setData(arrSubCategory)

                val rv_sub_category = findViewById<RecyclerView>(R.id.rv_sub_category)

                rv_sub_category.layoutManager = LinearLayoutManager(this@HomeActivity,LinearLayoutManager.HORIZONTAL,false)
                rv_sub_category.adapter = subCategoryAdapter
            }


        }
    }
}