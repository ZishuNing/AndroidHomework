package com.example.recipeapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val btnGetStarted=findViewById<Button>(R.id.btnGetStarted)
        //点击btnGetStarted转到HomeActivity
        btnGetStarted.setOnClickListener {
            var Intent=Intent(this,HomeActivity::class.java)
            startActivity(Intent)
            finish()
        }
    }
}