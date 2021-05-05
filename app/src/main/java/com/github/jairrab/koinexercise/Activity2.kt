package com.github.jairrab.koinexercise

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.github.jairrab.koinexercise.databinding.Activity2Binding
import com.github.jairrab.viewbindingutility.viewBinding
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

class Activity2 : AppCompatActivity() {
    private val binding by viewBinding { Activity2Binding.inflate(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val uploadWorkRequest = OneTimeWorkRequestBuilder<UploadWorker>().build()

        WorkManager
            .getInstance(this)
            .enqueue(uploadWorkRequest)
    }
}

class UploadWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val helloRepo: HelloRepo,
) : Worker(appContext, workerParams) {
    override fun doWork(): Result {

        // Do the work here--in this case, upload the images.
        Log.v("koin_test", "Doing some work at $this")

        //helloRepo.sayHello()

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }
}

class HelloRepo {
    fun sayHello() {
        Log.v("koin_test", "Hello from $this")
    }
}

val workManagerModule = module {
    worker { UploadWorker(get(), get(), get()) }

    single {
        HelloRepo()
    }
}