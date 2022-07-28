package com.example.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis


class MainActivity : AppCompatActivity() {

    /**
     * There are 2 jobs here and second one waits the first one's result.
     * It is async and await pattern
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener {
            setNewText("Clicked")
            fakeApiRequest()
        }
    }


    private fun fakeApiRequest() {
        CoroutineScope(IO).launch {
            val executionTime = measureTimeMillis {
                val result1 = async {
                    println("debug: launching job1: ${Thread.currentThread().name}")
                    getResult1FromAPI()
                }.await()

                println("Debug: got result2: $result1")
                setTextOnMainThread(result1)

                val result2 = async {
                    println("debug: launching job2: ${Thread.currentThread().name}")
                    getResult2FromAPI(result1)
                }.await()

                println("Debug: got result2: $result2")
                setTextOnMainThread(result2)
            }

            println("Debug: total elapsed time: $executionTime ms")
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

    private suspend fun getResult2FromAPI(result1: String): String {
        delay(1700)
        if (result1 == "RESULT #1") {
            return "RESULT #2"
        }
        throw CancellationException("Result1 was incorrect...")
    }

}