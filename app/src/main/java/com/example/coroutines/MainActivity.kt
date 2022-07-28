package com.example.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    /**
     * runBlocking is just a coroutine scope. It is the with coroutine scope but only one difference
     * a coroutine scope works in an isolated area in the selected thread. This means that it only works in it's area
     * and does not block entire thread. However if you use runBlocking you block entire thread until the job complete
     * in the runBlocking scope. That is the difference
     * -----
     * a common use case of runBlocking is testing with jUnit. we use it in testing because we want to be make
     * sure nothing happens in the current thread with related coroutine scope
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener {
            main()
        }
    }

    private fun main() {

        // # job1
        CoroutineScope(Main).launch {
            println("debug: Starting job in thread: ${Thread.currentThread().name}")

            val result1 = getResult()
            println("debug: result1:$result1")

            val result2 = getResult()
            println("debug: result1:$result2")

            val result3 = getResult()
            println("debug: result1:$result3")

            val result4 = getResult()
            println("debug: result1:$result4")

            val result5 = getResult()
            println("debug: result1:$result5")
        }

        // # job 2
        CoroutineScope(Main).launch {
            delay(1000)
            runBlocking {
                println("debug: blocking thread ${Thread.currentThread().name}")
                delay(4000)
                println("debug: done blocking thread ${Thread.currentThread().name}")
            }
        }
    }

    private suspend fun getResult(): Int {
        delay(1000)
        return Random.nextInt(0, 100)
    }



}