package com.example.android_alcohol.timer

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Log.e
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.android_alcohol.BaseActivity

class SimpleWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        return Result.success()
    }

}

