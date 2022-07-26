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

    /**
     * common use cases of coroutines ( when you don't want to block UI thread )
     * Network calls ( retrofit )
     * Accessing local database of phone ( room )
     *  ---------
     *  this example is about getting data from an api ( in background thread and show it in UI )
     *  -------
     *  suspend means that we can use this function in a coroutine
     *  -------
     *  coroutine does not mean a thread. Coroutine means is a job which works on a thread. Many coroutine
     *  can work at the same time in a single thread.
     *  -----
     *  delay () and thread.sleep are very different thinks. Because if you use thread.sleep all thread
     *  will be blocked. This means that all coroutines inside this thread will block. But delay () only
     *  block related coroutine inside thread. şşşş
     *  --------
     *  coroutines are job. Coroutine scope is organize coroutines in a group. A coroutine scope can include
     *  more coroutine job.
     * --------
     * withContext method you can change a coroutine thread. This means that for ex: a coroutine
     * can start work in the IO thread, do some work, then can go on it's work on another
     * thread like Main. It can do some work again in the Main thread then can change
     * it's thread again.
     *
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**
         * there are 3 different coroutine scopes
         * IO -> (doing work on background thread)for background work like network and db calls
         * Main -> (doing work on main thread)for updating UI. For ex: you get data from local db and show it UI. When yo
         *          get your data from db you need to use IO scope, when you want to set it to the UI element like textView,
         *          you need to use Main Scope.
         * Default -> for any heavy computational work ( ex : filter a large list )
         *
         */

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
        /**
         * firstly we need to start a new coroutine in a new coroutine scope.
         */
        withContext(Main) {
            setNewText(input)
        }
    }

    private val RESULT_1 = "Result #1"
    private val RESULT_2 = "Result #2"

    private suspend fun fakeApiRequest() {
        val result1 = getResult1FromAPI()
        println("debug : $result1")
        setTextOnMainThread(result1)
        //----- result2 waits the end of result1
        val result2 = getResult2FromAPI()
        println("debug : $result2")
        setTextOnMainThread(result2)
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