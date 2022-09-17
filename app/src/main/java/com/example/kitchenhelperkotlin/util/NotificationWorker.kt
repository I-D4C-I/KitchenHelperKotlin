package com.example.kitchenhelperkotlin.util

import android.content.Context
import androidx.navigation.NavController
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(
    val context: Context,
    params: WorkerParameters,
) : Worker(context, params) {

    override fun doWork(): Result {
        NotificationHelper(context).sendNotification(
            inputData.getString("title").toString(),
            inputData.getString("description").toString(),
            inputData.getInt("notificationId", TEST_NOTIFICATION),
        )
        return Result.success()
    }

}