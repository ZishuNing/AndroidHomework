package com.example.recipeapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

//用BaseActivity封装协程，继承它的Activity类可以直接使用协程
open class BaseActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var job: Job//Job 一种用于管理协程生命周期的对象
    //CoroutineScope 接口定义了一个 coroutineContext 属性，用于指定协程的上下文。
    // 协程的上下文coroutineContext包含了协程调度器（CoroutineDispatcher）和协程作业（Job），分别决定了协程在哪个线程中执行以及如何管理协程的生命周期
    //下面这行的意思大概是 如果你想获得协程上下文CoroutineContext，我会返回job +Dispatchers.Main（即协程线程用主线程）
    override val coroutineContext:CoroutineContext get() = job +Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()//初始化job
        }
    override fun onDestroy() {
        super.onDestroy()
        job.cancel()//取消 job
    }
}