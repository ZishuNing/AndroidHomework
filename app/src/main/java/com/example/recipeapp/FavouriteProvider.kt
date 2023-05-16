package com.example.recipeapp

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.Environment
import com.example.recipeapp.entities.CategoryItems
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File


class FavouriteProvider : ContentProvider() {

    var hs :HashSet<String> = HashSet()
    private val favouriteTable = 1
    private val favouriteTableItem  =2
    private val authority = "com.example.recipeapp.provider"

    lateinit var file : File

    // Checks if a volume containing external storage is available
    // for read and write.
    fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        // 加入uri，直接查询的时候返回favourite表
        addURI(authority, "favourite", favouriteTable)
        addURI(authority, "favourite/#", favouriteTableItem)
    }




    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        when(sUriMatcher.match(uri)){
            favouriteTable -> {
                hs.clear()
                flush()
                return 1
            }
            favouriteTableItem -> {
                val id = uri.lastPathSegment
                hs.remove(id)
                flush()
                return 1
            }
            else ->{
                return 0
            }
        }
    }

    override fun getType(uri: Uri): String? {

        TODO(
            "don't use this shit"
        )
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        when(sUriMatcher.match(uri)){

            // 将传入的一个Meal的id字符串加入到hs中
            favouriteTable -> {
                values?.getAsString("id")?.let { hs.add(it) }
                flush()
                return Uri.parse(
                    authority + values?.getAsString(
                        "id"
                    )
                )

            }
            else ->{

                return null
            }

        }

    }


    // 将hs持久化
    fun flush(){


        if(file.canWrite()){
            val gson=Gson()
            file.writeText(gson.toJson(hs,object :TypeToken<HashSet<String>>(){}.type))
        }else{
            throw java.lang.Exception("file无法访问")
        }


    }

    override fun onCreate(): Boolean {
        if(hs == null){
            hs = HashSet<String>()
        }


        if(isExternalStorageWritable()){
            // 允许访问文件
            file = File(context?.externalCacheDir, "favourite.json")


            if(file.isFile){

            }else{
                file.createNewFile()
            }

            if(file.canRead()){
                val data = file.readText()

                val gson=Gson()
                if(data.isEmpty()) {
                    hs = HashSet<String>()

                }else{
                    hs = gson.fromJson(data,object :TypeToken<HashSet<String>>(){}.type)
                }

            }


        }

        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        when(sUriMatcher.match(uri)){
            favouriteTable -> {
                // 除了Uri，其他参数都不需要
                val arr = hs.toTypedArray()
                val martixCursor = MatrixCursor(arrayOf("id"))
                for (i in arr.indices) {
                    martixCursor.addRow(arrayOf(arr[i]))
                }

                return martixCursor
            }
            // query 这个特定的item可以知道用户有没有喜欢这个食物，通过查看返回值是否为NULL
            favouriteTableItem->{
                var id : String? = uri.lastPathSegment;
                if(hs.contains(id)){
                    val martixCursor = MatrixCursor(arrayOf("id"))
                    martixCursor.addRow(arrayOf(id))
                    return martixCursor
                }
                else{
                    return null
                }
            }

            else ->{

                return null
            }

        }
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        TODO("can't be updated")
    }
}