package com.example.travelpal.ui.manager

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.travelpal.data.Location
import com.example.travelpal.data.TravelEntity
import com.example.travelpal.repository.LocationRepository
import com.example.travelpal.repository.TravelRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date
import androidx.core.app.NotificationCompat
import com.example.travelpal.MainActivity
import com.example.travelpal.R

class LiveTrackingManager(private val context: Context)  {
    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private lateinit var locationCallback: LocationCallback
    private var locationRequest: LocationRequest? = null

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            sendNotification()
            val randomDelay = (5..15).random() * 1000L // random delay between 5 and 15 seconds
            handler.postDelayed(this, randomDelay)
        }
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "com.example.travelpal.NOTIFICATION_CHANNEL"
    }

    private val locationRepository: LocationRepository by lazy {
        LocationRepository(context)
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setWaitForAccurateLocation(true)
            .setMinUpdateIntervalMillis(5000)
            .setMaxUpdateDelayMillis(15000)
            .build()
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(travelEntityId: Long) {
        createLocationRequest()
        locationCallback = object : LocationCallback() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    saveLocationPoint(location, travelEntityId)
                }
            }
        }

        locationRequest?.let {
            fusedLocationClient.requestLocationUpdates(
                it,
                locationCallback,
                Looper.getMainLooper())
        }
        handler.post(runnable)
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        handler.removeCallbacks(runnable)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveLocationPoint(location: android.location.Location, travelEntityId: Long) {
        val newLocation = Location(
            travelEntryId = travelEntityId,
            latitude = location.latitude,
            longitude = location.longitude,
            visitDate = LocalDateTime.now().toString(),
            name = null
        )
        locationRepository.createLocation(newLocation)
        println(newLocation)
    }


//    val locationUpdateReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            val location = intent.getParcelableExtra<Location>("location")
//            // Do something with the new location.
//        }
//    }
//
//    LocalBroadcastManager.getInstance(context).registerReceiver(
//    locationUpdateReceiver,
//    IntentFilter("LocationUpdated")
//    )

    private fun sendNotification() {
        val notificationManager = NotificationManagerCompat.from(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Take a picture")
            .setContentText("Please take a picture for your journey.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

//        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}