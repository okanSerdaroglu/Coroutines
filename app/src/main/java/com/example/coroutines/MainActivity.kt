package com.example.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    /**
     * Global scope does not sync with the parent job. As in the example
     * when you start parent job with 2 different jobs, if children jobs
     * are in GlobalScope.
     * ------
     * it is not commonly used, but if you want to think about common use
     * case of GlobalScope is maybe, you want to learn something in your app
     * if is it happened or not ( Like an analytics track etc ) We can say that
     * if you use global scope, this job is independent from anything else.
     *
     */

    private val TAG: String = "AppDebug"

    lateinit var parentJob: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main()

        button.setOnClickListener {
            parentJob.cancel()
        }
    }

    private fun main() {
        val startTime = System.currentTimeMillis()
        printLn("Starting parent job...")
        parentJob = CoroutineScope(Main).launch {
            GlobalScope.launch {
                work(1)
            }
            GlobalScope.launch {
                work(2)
            }
        }

        parentJob.invokeOnCompletion { throwable ->
            if (throwable != null) {
                printLn("{$throwable.message.orEmpty()} job cancelled")
            } else {
                printLn("Done in ${System.currentTimeMillis() - startTime} ms")
            }
        }
    }

    private suspend fun work(i: Int) {
        delay(3000)
        println("work $i done. ${Thread.currentThread().name}")
    }

    private fun printLn(message: String) {
        Log.d(TAG, message)
    }
}