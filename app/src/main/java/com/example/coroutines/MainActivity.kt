package com.example.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

class MainActivity : AppCompatActivity() {

    /**
     * the aim of this, start a job in a scope and get an action
     * only about this job.
     * when you
     */

    private val PROGRESS_MAX = 100
    private val PROGRESS_START = 0
    private val jobTime = 4000 // ms
    private lateinit var job: CompletableJob

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        job_button.setOnClickListener {
            if (!::job.isInitialized) {
                /** it is unique control for lateinit variables */
                initJob()
            }
            job_progress_bar.startJobOrCancel(job)
        }
    }

    private fun initJob() {
        job_button.text = "Start Job #1"
        updateJobCompleteTextView("") /** update textView in main thread */
        job = Job() /** create job here */
        job.invokeOnCompletion { /** set invokeOnCompletion interface to your job to get cancellation result.*/
            /** when job cancels this method work */
            it?.message.let { msg ->
                var message = msg
                if (message.isNullOrBlank()) {
                    message = "Unknown cancellation error"
                }
                println("$job was cancelled because of $message")
                showToast(message)
            }
        }
        job_progress_bar.max = PROGRESS_MAX
        job_progress_bar.progress = PROGRESS_START
    }

    /**
     * CoroutineScope(IO + job) means that add job into IO scope. This
     * creates an isolated area for your job in the IO Scope.
     * If you cancel IO scope all jobs will be cancelled in IO scope
     * which is not very good. When you want to cancel only job, just
     * call job.cancel. This helps you to cancel only one job in the IO scope
     */
    private fun ProgressBar.startJobOrCancel(job: Job) {
        if (this.progress > 0) {
            println("$job is already active. Cancelling ...")
            resetJob()
        } else {
            job_button.text = "Cancel Job #1"
            CoroutineScope(IO + job).launch {
                println("coroutine $this is activated with job $job")
                for (i in PROGRESS_START..PROGRESS_MAX) {
                    delay((jobTime / PROGRESS_MAX).toLong())
                    this@startJobOrCancel.progress = i
                }
                updateJobCompleteTextView("Job is complete")
            }
        }
    }

    private fun updateJobCompleteTextView(text: String) {
        GlobalScope.launch(Main) {
            job_complete_text.text = text
        }
    }

    private fun resetJob() {
        if (job.isActive || job.isCompleted) {
            job.cancel(CancellationException("Resetting job.."))
        }
        initJob()
    }

    private fun showToast(text: String) {
        GlobalScope.launch(Main) {
            Toast.makeText(this@MainActivity, text, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}