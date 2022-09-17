package com.example.kitchenhelperkotlin.util

import android.content.Context
import android.provider.SyncStateContract
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavController
import com.example.kitchenhelperkotlin.KHApplication
import com.example.kitchenhelperkotlin.R

class NotificationHelper(val context: Context, private val navController: NavController) {

    fun sendNotification(
        title: String = "Example Title",
        description: String = "Example Description",
        notificationId: Int = TOBUY_NOTIFICATION
    ) {
        val pendingIntent = navController
            .createDeepLink()
            .setDestination(R.id.toBuyFragment)
            .createPendingIntent()

        val builder = NotificationCompat.Builder(context, KHApplication.CHANNEL_MAIN_ID)
            .setSmallIcon(R.drawable.ic_notification) // TODO: Заменить иконку на иконку приложения
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }

}