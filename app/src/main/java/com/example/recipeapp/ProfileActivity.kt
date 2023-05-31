package com.example.recipeapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

class ProfileActivity : AppCompatActivity() {
    // 声明对话框变量
    lateinit var dialog: AlertDialog
    private val takePhoto = 1
    private val fromAlbum = 2
    private lateinit var imageUri: Uri
    private lateinit var outputImage: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        // 请求相机和存储权限
        requestPermissions()

        val backButton = findViewById<Button>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

        val showBtn = findViewById<Button>(R.id.show_dialog_button)
        showBtn.setOnClickListener {
            // 创建对话框
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val layout = inflater.inflate(R.layout.activity_dialog_select, null)
            builder.setView(layout)

            // 获取取消按钮并设置点击事件处理程序
            val cancelButton = layout.findViewById<Button>(R.id.cancel)
            val takePhotoBtn= layout.findViewById<Button>(R.id.takePhotoBtn)
            val fromAlbumBtn= layout.findViewById<Button>(R.id.fromAlbumBtn)
            cancelButton.setOnClickListener {
                //关闭对话框
                dialog.dismiss()
            }
            takePhotoBtn.setOnClickListener {
                // 创建 File 对象，用于存储拍照后的图片
                outputImage = File(externalCacheDir, "output_image.jpg")
                if (outputImage.exists()) outputImage.delete()
                outputImage.createNewFile()
                Log.d("ProfileActivityadhbadb", "outputImage path: " + outputImage.absolutePath)
                imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    FileProvider.getUriForFile(
                        this,
                        "nzsZrh",
                        outputImage
                    )
                } else {
                    Uri.fromFile(outputImage)
                }
                // 启动相机程序
                val intent = Intent("android.media.action.IMAGE_CAPTURE")
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(intent, takePhoto)
            }
            fromAlbumBtn.setOnClickListener {
                // 打开文件选择器
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                // 指定只显示图片
                intent.type = "image/*"
                startActivityForResult(intent, fromAlbum)
            }

            // 创建对话框实例并显示
            dialog = builder.create()
            dialog.setCanceledOnTouchOutside(true)
            dialog.show()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val imageView= findViewById<ImageView>(R.id.imageView)
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            takePhoto -> {
                if (resultCode == Activity.RESULT_OK) {
                    // 将拍摄的照片显示出来
                    val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
                    val rotatedBitmap = rotateIfRequired(bitmap)
                    saveBitmap(rotatedBitmap)
                    imageView.setImageBitmap(rotatedBitmap)
                }
            }
            fromAlbum -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let { uri ->
                        // 将选择的图片显示
                        val bitmap = getBitmapFromUri(uri)
                        imageView.setImageBitmap(bitmap)
                    }
                }
            }
        }
    }
    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return contentResolver.openFileDescriptor(uri, "r")?.use {
            BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
        }
    }
    private fun saveBitmap(bmp: Bitmap) {
        val tags = "saveBitmap"
        val fileName = "${System.currentTimeMillis()}.jpg"
        val dirPath = Environment.getExternalStorageDirectory().toString() + "/DCIM/"
        val dir = File(dirPath)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val file = File(dirPath + fileName)
        var fos: FileOutputStream? = null;
        try {
            fos = FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            Log.d(tags, "saved!")
        } catch (e: Exception) {
            Log.d(tags, "error!" + e.message)
        } finally {
            fos?.flush()
            fos?.close()
        }
    }


    private fun rotateIfRequired(bitmap: Bitmap): Bitmap {
        val exif = ExifInterface(outputImage.path)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270)
            else -> bitmap
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        bitmap.recycle() // 将不再需要的 Bitmap 对象回收
        return rotatedBitmap
    }
    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val notGrantedPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (notGrantedPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, notGrantedPermissions.toTypedArray(), 0)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0 && grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            // 权限已被授予，可以开始使用相机和存储功能了
        } else {
            Toast.makeText(this, "请授予相机和存储权限", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}