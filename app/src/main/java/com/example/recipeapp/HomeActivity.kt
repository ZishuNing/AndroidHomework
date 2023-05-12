package com.example.recipeapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.adapter.MainCategoryAdapter
import com.example.recipeapp.adapter.SubCategoryAdapter
import com.example.recipeapp.database.RecipeDatabase
import com.example.recipeapp.entities.Category
import com.example.recipeapp.entities.CategoryItems
import com.example.recipeapp.entities.MealsItems
import com.example.recipeapp.entities.Recipes
import kotlinx.coroutines.launch

//主界面
class HomeActivity : BaseActivity() {

    //主次菜单菜品数据
    var arrMainCategory = ArrayList<CategoryItems>()
    var arrSubCategory = ArrayList<MealsItems>()

    //主次菜单RecyclerView的Adapter
    var mainCategoryAdapter = MainCategoryAdapter()
    var subCategoryAdapter = SubCategoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        getDataFromDb()

        mainCategoryAdapter.setClickListener(onCLicked)
//        subCategoryAdapter.setClickListener(onCLickedSubItem)

    }

    // 函数指针，点击之后执行onClicked函数
    private val onCLicked  = object : MainCategoryAdapter.OnItemClickListener{
        override fun onClicked(categoryName: String) {
            getMealDataFromDb(categoryName)
        }
    }

    //  函数指针，点击之后执行onClicked函数
//    private val onCLickedSubItem  = object : SubCategoryAdapter.OnItemClickListener{
//        override fun onClicked(id: String) {
//            var intent = Intent(this@HomeActivity,DetailActivity::class.java)
//            intent.putExtra("id",id)
//            startActivity(intent)
//        }
//    }






    private fun getDataFromDb(){
        launch {
            this.let {

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