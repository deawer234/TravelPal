package com.example.travelpal.ui.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.example.travelpal.R
import com.example.travelpal.repository.LocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class TrackerService() : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient

    private var totalDistance = 0f
    private var startTime = 0L
    private var totalTime = 0L

    private var travelEntityId = -1L

    private var speed = 0f
    private var stepCount = 0
    private var altitude = 0.0

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): TrackerService = this@TrackerService
    }
    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }

    private val locationRepository: LocationRepository by lazy {
        LocationRepository(applicationContext)
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = LocationClientImpl(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        travelEntityId = intent?.getLongExtra("travelEntityId", -1L) ?: -1L
        when(intent?.action){
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Tracking location...")
            .setContentText("Location: null")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)
            .setOnlyAlertOnce(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        locationClient
            .getLocationUpdates(10000L)
            .catch { e -> e.printStackTrace() }
            .onEach {
                it.entries.lastOrNull()?.let { entry ->

                    stepCount = entry.key
                    entry.value.distanceTo(it.entries.firstOrNull()?.value ?: entry.value)
                    totalDistance += entry.value.distanceTo(it.entries.first().value)
                    this.speed = entry.value.speed
                    this.altitude = entry.value.altitude
                    saveLocationPoint(entry.value, travelEntityId)
                    notification.setContentText("Location: ${entry.value.latitude}, ${entry.value.longitude}")

                }
                notificationManager.notify(1, notification.build())
            }
            .launchIn(serviceScope)
        startForeground(1, notification.build())
    }


    private fun stop() {
        stopForeground(true)
        stopSelf()
    }


    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun saveLocationPoint(location: Location, travelEntityId: Long) {
        val newLocation = com.example.travelpal.data.Location(
            travelEntryId = travelEntityId,
            latitude = location.latitude,
            longitude = location.longitude,
            visitDate = System.currentTimeMillis().toString(),
            name = null
        )
        locationRepository.createLocation(newLocation)
        println(newLocation)
    }

    fun getStepCount(): Int {
        return stepCount
    }

    fun getTotalDistance(): Float {
        return totalDistance
    }

    fun getAverageSpeed(): Float {
        return speed
    }
    fun getLastAltitude(): Double {
        return altitude
    }


}