package com.example.kitchenhelperkotlin.util

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.kitchenhelperkotlin.R

class NotificationHelper(val context: Context) {


    fun sendNotification(
        title: String = "Example Title",
        description: String = "Example Description",
        notificationId: Int = TOBUY_NOTIFICATION
    ) {
        //Потом можно добавить новый параметр и заменить это на when
        val uri = Uri.parse("myapp://example.com/tobuylist")

        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        /*
        val pendingIntent = navController
            .createDeepLink()
            .setDestination(R.id.toBuyFragment)
            .createPendingIntent()
        */

        val builder = NotificationCompat.Builder(context, CHANNEL_MAIN_ID)
            .setSmallIcon(R.drawable.ic_notification) // TODO: Заменить иконку на иконку приложения
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notify(notificationId, builder.build())
        }
    }
}