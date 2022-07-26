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
import kotlinx.coroutines.withTimeoutOrNull


class MainActivity : ComponentActivity() {


    private val JOB_TIMEOUT = 3900L

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
            val job = withTimeoutOrNull(JOB_TIMEOUT) {
                val result1 = getResult1FromAPI() // wait
                setTextOnMainThread(result1)

                val result2 = getResult2FromAPI() // wait
                setTextOnMainThread(result2)
            }

            if (job == null) {
                val cancelMessage = "Cancelling job... Job takes longer than $JOB_TIMEOUT ms"
                println("debug : $cancelMessage")
                setTextOnMainThread(cancelMessage)
            }

            /**
             * total time for this job is 4000 ms. However our timeout for this
             * job is 3900 ms. That's why withTimeoutOrNull function returns null here
             */
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