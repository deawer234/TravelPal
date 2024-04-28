package com.example.travelpal.ui.manager

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContentProviderCompat.requireContext
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


class LiveTrackingManager(private val context: Context, private val travelEntity: TravelEntity)  {
    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private lateinit var locationCallback: LocationCallback
    private var locationRequest: LocationRequest? = null

    private var lastLocation: Location? = null
    private var totalDistance = 0f
    private var startTime = 0L
    private var totalTime = 0L

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            sendNotification()
            val randomDelay = (5..15).random() * 10000L // random delay between 50 and 150 seconds
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
    private fun sendNotification() {
        val notificationManager = NotificationManagerCompat.from(context)

        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra("travelEntityId", travelEntity.id)

        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Take a picture")
            .setContentText("Please take a picture for your journey.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    @SuppressLint("MissingPermission")
    fun startLocationUpdates(travelEntityId: Long) {
        startTime = System.currentTimeMillis()
        createLocationRequest()

        locationCallback = object : LocationCallback() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations) {

                    lastLocation?.let {
                        totalDistance += it.distanceTo(location)
                    }
                    lastLocation = location

                    totalTime = System.currentTimeMillis() - startTime

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
        val newLocation = com.example.travelpal.data.Location(
            travelEntryId = travelEntityId,
            latitude = location.latitude,
            longitude = location.longitude,
            visitDate = LocalDateTime.now().toString(),
            name = null
        )
        locationRepository.createLocation(newLocation)
        println(newLocation)
    }

    fun getTotalDistance(): Float {
        return totalDistance
    }

    fun getAverageSpeed(): Float {
        return lastLocation?.speed ?: 0f
    }
    fun getLastAltitude(): Double {
        return lastLocation?.altitude ?: 0.0
    }

    fun getLastLocation(): Location? {
        return lastLocation
    }

}