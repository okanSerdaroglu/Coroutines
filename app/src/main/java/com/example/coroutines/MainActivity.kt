package com.example.coroutines

import android.os.Bundle
import androidx.activity.ComponentActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            CoroutineScope(IO).launch {
                fakeApiRequest()
            }
        }
    }

    private fun setNewText(input: String) {
        val newText = textView.text.toString() + "\n$input"
        textView.text = newText
    }

    private suspend fun setTextOnMainThread(input: String) {
        withContext(Main) {
            setNewText(input)
        }
    }

    private val RESULT_1 = "Result #1"
    private val RESULT_2 = "Result #2"

    private suspend fun fakeApiRequest() {
        /**
         * we can assign a coroutine scope as a job
         */
        withContext(IO) {
            val job = launch {
                val result1 = getResult1FromAPI()
                setTextOnMainThread(result1)

                val result2 = getResult2FromAPI()
                setTextOnMainThread(result2)
            }
        }
    }

    private suspend fun getResult1FromAPI(): String {
        logThread("getResult1FromAPI")
        delay(2000)
        return RESULT_1
    }


    private suspend fun getResult2FromAPI(): String {
        logThread("getResult2FromAPI")
        delay(2000)
        return RESULT_2
    }

    private fun logThread(methodName: String) {
        println("debug : ${methodName}: ${Thread.currentThread().name}")
    }
}