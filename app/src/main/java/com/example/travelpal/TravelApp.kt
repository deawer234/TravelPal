package com.example.travelpal

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build


class TravelApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Location"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("location", name, importance)

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            val channelCamera =
                NotificationChannel("camera", "Camera", NotificationManager.IMPORTANCE_DEFAULT)

            notificationManager.createNotificationChannel(channelCamera)
        }
    }
}