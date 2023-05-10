package com.example.recipeapp.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.R
import com.example.recipeapp.entities.Recipes
import android.widget.TextView

//显示主要菜品的列表
class MainCategoryAdapter() : RecyclerView.Adapter<MainCategoryAdapter.RecipeViewHolder>(){

    //主要菜品列表数据
    var arrMainCategory = ArrayList<Recipes>()

    //用来显示 RecyclerView 中的单个项
    class RecipeViewHolder(view: View): RecyclerView.ViewHolder(view){

    }

    //接受来自activity的数据
    fun setData(arrData : List<Recipes>){
        arrMainCategory = arrData as ArrayList<Recipes>
    }

    //使用 LayoutInflater 来从 R.layout.item_rv_main_category 布局文件中创建一个 View 对象，并将其传递给 RecipeViewHolder 的构造函数，从而创建一个 RecipeViewHolder 对象
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        return RecipeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_rv_main_category,parent,false))
    }

    //当前菜品数量
    override fun getItemCount(): Int {
        return arrMainCategory.size
    }

    //给RecyclerView的每个菜品提供对应的菜名
    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val tvDishName = holder.itemView.findViewById<TextView>(R.id.tv_dish_name)
        tvDishName.text = arrMainCategory[position].dishName
    }
}