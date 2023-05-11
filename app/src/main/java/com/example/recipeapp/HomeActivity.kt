package com.example.recipeapp

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.adapter.MainCategoryAdapter
import com.example.recipeapp.adapter.SubCategoryAdapter
import com.example.recipeapp.entities.Recipes

//主界面
class HomeActivity : BaseActivity() {

    //主次菜单菜品数据
    var arrMainCategory = ArrayList<Recipes>()
    var arrSubCategory = ArrayList<Recipes>()
    //主次菜单RecyclerView的Adapter
    var mainCategoryAdapter = MainCategoryAdapter()
    var subCategoryAdapter = SubCategoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // 给主次菜单添加数据（暂时这么写）
        arrMainCategory.add(Recipes(id = 1, dishName = "Beef"))
        arrMainCategory.add(Recipes(id = 2, dishName = "Chicken"))
        arrMainCategory.add(Recipes(id = 3, dishName = "Dessert"))
        arrMainCategory.add(Recipes(id = 4, dishName = "Lamb"))
        mainCategoryAdapter.setData(arrMainCategory)

        arrSubCategory.add(Recipes(id = 1, dishName = "Beef and mustard pie"))
        arrSubCategory.add(Recipes(id = 2, dishName = "Chicken and mushroom hotpot"))
        arrSubCategory.add(Recipes(id = 3, dishName = "Banana pancakes"))
        arrSubCategory.add(Recipes(id = 4, dishName = "Kapsalon"))
        subCategoryAdapter.setData(arrSubCategory)
        //配置主次菜单RecyclerView
        val rvMainCategory = findViewById<RecyclerView>(R.id.rv_main_category)
        rvMainCategory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)//HORIZONTAL表示子项将水平排列，false表示从左向右排列
        rvMainCategory.adapter = mainCategoryAdapter

        val rvSubCategory = findViewById<RecyclerView>(R.id.rv_sub_category)
        rvSubCategory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)//HORIZONTAL表示子项将水平排列，false表示从左向右排列
        rvSubCategory.adapter = subCategoryAdapter
    }
}