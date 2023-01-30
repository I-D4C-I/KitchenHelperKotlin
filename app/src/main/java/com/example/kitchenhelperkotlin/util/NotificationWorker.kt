package com.example.kitchenhelperkotlin.util

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class NotificationWorker(
    val context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        NotificationHelper(context).sendNotification(
            inputData.getString("title").toString(),
            inputData.getString("description").toString(),
            inputData.getInt("notificationId", TEST_NOTIFICATION),
        )
        return Result.success()
    }
}