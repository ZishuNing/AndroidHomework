package com.example.recipeapp.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.R
import com.example.recipeapp.entities.Recipes
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.recipeapp.entities.CategoryItems

//显示主要菜品的列表
class MainCategoryAdapter() : RecyclerView.Adapter<MainCategoryAdapter.RecipeViewHolder>(){

    var listener: OnItemClickListener? = null
    var ctx: Context?= null

    //主要菜品列表数据
    var arrMainCategory = ArrayList<CategoryItems>()

    //用来显示 RecyclerView 中的单个项
    class RecipeViewHolder(view: View): RecyclerView.ViewHolder(view){

    }

    fun setClickListener(listener1: OnItemClickListener){
        listener = listener1
    }



    //接受来自activity的数据
    fun setData(arrData : List<CategoryItems>){
        arrMainCategory = arrData as ArrayList<CategoryItems>
    }

    //使用 LayoutInflater 来从 R.layout.item_rv_main_category 布局文件中创建一个 View 对象，并将其传递给 RecipeViewHolder 的构造函数，从而创建一个 RecipeViewHolder 对象
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        ctx = parent.context
        return RecipeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_rv_main_category,parent,false))
    }

    //当前菜品数量
    override fun getItemCount(): Int {
        return arrMainCategory.size
    }

    //给RecyclerView的每个菜品提供对应的菜名
    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val tvDishName = holder.itemView.findViewById<TextView>(R.id.tv_dish_name)
        tvDishName.text = arrMainCategory[position].strcategory

        Glide.with(ctx!!).load(arrMainCategory[position].strcategorythumb).into(holder.itemView.findViewById(R.id.img_dish))
        holder.itemView.rootView.setOnClickListener {
            listener!!.onClicked(arrMainCategory[position].strcategory)
        }
    }

    interface OnItemClickListener{
        fun onClicked(categoryName:String)
    }
}