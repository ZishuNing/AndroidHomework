package com.example.recipeapp.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.R
import com.example.recipeapp.entities.Recipes
import android.widget.TextView

//注释见MainCategoryAdapter，格式类似
class SubCategoryAdapter() : RecyclerView.Adapter<SubCategoryAdapter.RecipeViewHolder>(){

    var arrSubCategory = ArrayList<Recipes>()

    class RecipeViewHolder(view: View): RecyclerView.ViewHolder(view){

    }

    fun setData(arrData : List<Recipes>){
        arrSubCategory = arrData as ArrayList<Recipes>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        return RecipeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_rv_sub_category,parent,false))
    }

    override fun getItemCount(): Int {
        return arrSubCategory.size
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val tvDishName = holder.itemView.findViewById<TextView>(R.id.tv_dish_name)
        tvDishName.text = arrSubCategory[position].dishName
    }
}