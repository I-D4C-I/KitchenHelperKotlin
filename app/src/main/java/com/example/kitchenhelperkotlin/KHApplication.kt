package com.example.kitchenhelperkotlin

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.kitchenhelperkotlin.util.CHANNEL_MAIN_ID
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KHApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        createNotificationChannels()
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mainChannel = NotificationChannel(
                CHANNEL_MAIN_ID,
                "Main_Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "This is main Channel"
            }
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mainChannel)
        }
    }
}