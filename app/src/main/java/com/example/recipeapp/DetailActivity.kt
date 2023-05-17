package com.example.recipeapp


import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.ContentValues
import android.content.Intent
import android.content.ServiceConnection
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.example.recipeapp.Interfaces.GetDataService
import com.example.recipeapp.database.RecipeDatabase
import com.example.recipeapp.entities.*
import com.example.recipeapp.retrofitclient.RetrofitClientInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//Meal界面
@SuppressLint("HandlerLeak")
class DetailActivity : BaseActivity() {

    var youtubeLink = ""
    var myid : String? = null
    var favCursor : Cursor? = null
    var binder : CacheService.CacheBinder? = null



//    val show = 1
//
//    var handler = object : Handler(Looper.getMainLooper()){
//        override fun handleMessage(msg: android.os.Message) {
//            super.handleMessage(msg)
//            when(msg.what){
//                show -> {
//                    val meal = msg.obj as MealsEntity
//                    showDetail(meal)
//                }
//            }
//        }
//    }

    private val connection = object :ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binder = service as CacheService.CacheBinder

            val meal =binder!!.getCache(myid!!)
            if(meal != null){
                showDetail(meal)
                Log.d("DetailActivity", "onServiceConnected: 从缓存中获取数据")

            }else{
                Log.d("DetailActivity", "onServiceConnected: 从网络中获取数据")
                getSpecificItem(myid!!)
            }
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("DetailActivity", "onServiceDisconnected: ")
        }
    }



    // 从HomeActivity过来，展示具体的食物信息
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        myid = intent.getStringExtra("id")



        // 绑定服务，从服务里面获取数据，或从网络里面获取数据并缓存起来
        val intent = Intent(this, CacheService::class.java)
        bindService(intent, connection, BIND_AUTO_CREATE)




        val imgToolbarBtnBack = findViewById<ImageView>(R.id.imgToolbarBtnBack)
        imgToolbarBtnBack.setOnClickListener {
            finish()
        }


        val btnYoutube = findViewById<Button>(R.id.btnYoutube)
        btnYoutube.setOnClickListener {
            val uri = Uri.parse(youtubeLink)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        val favourite = findViewById<ImageButton>(R.id.imgToolbarBtnFav)
        favourite.setOnClickListener{



            // 不喜欢的情况
            if(favCursor == null){
                // 改成喜欢
                var contentvalues = ContentValues()
                contentvalues.put("id", myid)
                contentResolver.insert(Uri.parse("content://com.example.recipeapp.provider/favourite"), contentvalues)
                favCursor =  contentResolver.query(Uri.parse("content://com.example.recipeapp.provider/favourite/$myid"), null, null, null, null)
                if(favCursor==null){
                    Toast.makeText(this, "provider insert wrong", Toast.LENGTH_SHORT).show()
                }
            }else{
                contentResolver.delete(Uri.parse("content://com.example.recipeapp.provider/favourite/$myid"), null, null)
                favCursor = null
            }
            setFavourite()

        }

    }


    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)// 解绑，因为服务是在HomeActivity里面启动的，所以在这里解绑，不会使它消失，除非调用stopService
    }

    // 查看是否被喜欢了
    fun setFavourite(){
        val favourite = findViewById<ImageButton>(R.id.imgToolbarBtnFav)
        // 不喜欢
        if(favCursor==null){
            favourite.setBackgroundResource(R.drawable.btn_bg2)
        }else{
            // 喜欢
            favourite.setBackgroundResource(R.drawable.btn_bg2r)
        }
    }


    fun showDetail(meal: MealsEntity) {
        val imgItem = findViewById<ImageView>(R.id.imgItem)
        Glide.with(this@DetailActivity).load(meal.strmealthumb).into(imgItem)

        val tvCategory = findViewById<TextView>(R.id.tvCategory)
        tvCategory.text = meal.strmeal


        // 写入内容
        var ingredient = "${meal.stringredient1}      ${meal.strmeasure1}\n" +
                "${meal.stringredient2}      ${meal.strmeasure2}\n" +
                "${meal.stringredient3}      ${meal.strmeasure3}\n" +
                "${meal.stringredient4}      ${meal.strmeasure4}\n" +
                "${meal.stringredient5}      ${meal.strmeasure5}\n" +
                "${meal.stringredient6}      ${meal.strmeasure6}\n" +
                "${meal.stringredient7}      ${meal.strmeasure7}\n" +
                "${meal.stringredient8}      ${meal.strmeasure8}\n" +
                "${meal.stringredient9}      ${meal.strmeasure9}\n" +
                "${meal.stringredient10}      ${meal.strmeasure10}\n" +
                "${meal.stringredient11}      ${meal.strmeasure11}\n" +
                "${meal.stringredient12}      ${meal.strmeasure12}\n" +
                "${meal.stringredient13}      ${meal.strmeasure13}\n" +
                "${meal.stringredient14}      ${meal.strmeasure14}\n" +
                "${meal.stringredient15}      ${meal.strmeasure15}\n" +
                "${meal.stringredient16}      ${meal.strmeasure16}\n" +
                "${meal.stringredient17}      ${meal.strmeasure17}\n" +
                "${meal.stringredient18}      ${meal.strmeasure18}\n" +
                "${meal.stringredient19}      ${meal.strmeasure19}\n" +
                "${meal.stringredient20}      ${meal.strmeasure20}\n"

        val tvIngredients = findViewById<TextView>(R.id.tvIngredients)
        tvIngredients.text = ingredient

        val tvInstructions = findViewById<TextView>(R.id.tvInstructions)
        tvInstructions.text = meal.strinstructions

        if (meal.strsource != null){
            youtubeLink = meal.strsource
        }else{
            val btnYoutube = findViewById<Button>(R.id.btnYoutube)
            btnYoutube.visibility = View.GONE
        }

        favCursor = contentResolver.query(Uri.parse("content://com.example.recipeapp.provider/favourite/${myid}"), null, null, null, null)
        setFavourite()
    }
    fun getSpecificItem(id:String) {
        // 获取网络或缓存服务获取数据的单例



        myid = id
        val service = RetrofitClientInstance.retrofitInstance!!.create(GetDataService::class.java)
        val call = service.getSpecificItem(id)

        // 通过id获取数据
        call.enqueue(object : Callback<MealResponse> {
            override fun onFailure(call: Call<MealResponse>, t: Throwable) {

                Toast.makeText(this@DetailActivity, "Something went wrong" + t.message, Toast.LENGTH_SHORT )
                    .show()
            }

            override fun onResponse(
                call: Call<MealResponse>,
                response: Response<MealResponse>
            ) {

                showDetail(response.body()!!.mealsEntity[0])
                binder!!.setCache(id, response.body()!!.mealsEntity[0])

            }

        })
    }



}