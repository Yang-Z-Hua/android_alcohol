package com.example.android_alcohol.publicMethod

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.util.Log
import java.text.SimpleDateFormat
//时间戳转格式化时间
object methods {

    private val activities = ArrayList<Activity>()

    fun addActivity(activity: Activity) {
        activities.add(activity)
    }

    fun removeActivity(activity: Activity) {
        activities.remove(activity)
    }

    fun finishAll() {
        for (activity in activities) {
            if (!activity.isFinishing) {
                activity.finish()
            }
        }
        activities.clear()
    }
}