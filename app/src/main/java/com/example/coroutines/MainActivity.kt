package com.example.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener {
            setNewText("Clicked")
            fakeApiRequest()
        }
    }

    /**
     * async and await pattern
     * it is totally same with job pattern. There is only one difference. With this pattern
     * you can access the result. In job pattern it is available it the job like that
     * val job = launch {
     *    val result = getResult1FromAPI() -> result is available only in job. But in async pattern you can get it outside the job
     * }
     */
    private fun fakeApiRequest() {

        CoroutineScope(IO).launch {

            val executionTime = measureTimeMillis {
                val result1: Deferred<String> = async {
                    println("debug: launching job1:${Thread.currentThread().name}")
                    getResult1FromAPI()
                }

                val result2: Deferred<String> = async {
                    println("debug: launching job2:${Thread.currentThread().name}")
                    getResult2FromAPI()
                }
            }
            println("debug:total time elapsed:${executionTime}")
        }
    }

    private fun setNewText(input: String) {
        val newText = text.text.toString() + "\n" + input
        text.text = newText
    }

    private suspend fun setTextOnMainThread(input: String) {
        withContext(Main) {
            setNewText(input)
        }
    }

    private suspend fun getResult1FromAPI(): String {
        delay(1000)
        return "RESULT #1"
    }

    private suspend fun getResult2FromAPI(): String {
        delay(1700)
        return "RESULT #2"
    }

}