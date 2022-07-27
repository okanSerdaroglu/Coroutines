package com.example.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
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
     * in this case both jobs work at the same time.
     * if you use job1.join() this means that we want to wait
     * for ending job1 then job2 starts. You can check the times
     * from the logs
     */
    private fun fakeApiRequest() {

        val startTime = System.currentTimeMillis()

        val parentJob = CoroutineScope(IO).launch {
            val job1 = launch {
                val time1 = measureTimeMillis {
                    println("debug: launching job1 in thread: ${Thread.currentThread().name} ")
                    val result1 = getResult1FromAPI()
                    setTextOnMainThread(result1)
                }
                println("debug: completed job1 in $time1 ms.")
            }
            val job2 = launch {
                val time2 = measureTimeMillis {
                    println("debug: launching job2 in thread: ${Thread.currentThread().name} ")
                    val result2 = getResult2FromAPI()
                    setTextOnMainThread(result2)
                }
                println("debug: completed job1 in $time2 ms.")
            }
        }
        parentJob.invokeOnCompletion {
            println("debug: total elapsed time : ${System.currentTimeMillis() - startTime}")
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