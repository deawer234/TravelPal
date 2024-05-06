package com.example.travelpal.ui.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.example.travelpal.R
import com.example.travelpal.repository.LocationRepository
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.random.Random

class TrackerService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient
    private var notification: NotificationCompat.Builder? = null

    private var totalDistance = 0f
    private var startTime = 0L
    private var totalTime = 0L

    private var travelEntityId = -1L

    private lateinit var lastLocation: Location
    private var startingLocation: Location? = null

    val stepCountData = MutableLiveData<Int>()
    val totalDistanceData = MutableLiveData<Float>()
    val averageSpeedData = MutableLiveData<Float>()
    val lastAltitudeData = MutableLiveData<Double>()

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
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        travelEntityId = intent?.getLongExtra("travelEntityId", -1L) ?: -1L
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notification = NotificationCompat.Builder(this, "location")
                .setContentTitle("Tracking location...")
                .setContentText("Location: null")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
        }

//        schedulePhotoReminder()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        locationClient
            .getLocationUpdates(5000L)
            .catch { e -> e.printStackTrace() }
            .onEach {
                it.entries.lastOrNull()?.let { entry ->
                    if (startingLocation == null) {
                        startingLocation = entry.key
                        lastLocation = entry.key
                        startTime = System.currentTimeMillis()
                    }
                    stepCountData.postValue(entry.value)

                    totalDistance += entry.key.distanceTo(lastLocation)
                    totalDistanceData.postValue(totalDistance)

                    averageSpeedData.postValue(entry.key.speed)

                    lastAltitudeData.postValue(entry.key.altitude)

                    lastLocation = entry.key
                    saveLocationPoint(entry.key, travelEntityId)
                    notification?.setContentText("Location: ${entry.key.latitude}, ${entry.key.longitude}")

                }
                notificationManager.notify(1, notification?.build())
            }
            .launchIn(serviceScope)
        startForeground(1, notification?.build())

    }


    private fun stop() {
        stopForeground(true)
        stopSelf()
    }


    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

//    private fun schedulePhotoReminder() {
//        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val intent = Intent(this, PhotoNotificationReceiver::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//
//        val randomTime = Random.nextLong(1, 3) * 60 * 1000  // Random time between 1 and 60 minutes
//        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + randomTime, pendingIntent)
//    }

    private fun saveLocationPoint(location: Location, travelEntityId: Long) {
        val newLocation = com.example.travelpal.data.Location(
            travelEntryId = travelEntityId,
            latitude = location.latitude,
            longitude = location.longitude,
            visitDate = System.currentTimeMillis().toString(),
            elevation = location.altitude,
            traveled = totalDistance,
            speed = location.speed,
            steps = stepCountData.value ?: 0
        )
        locationRepository.createLocation(newLocation)
        println(newLocation)
    }

//    fun getStepCount(): Int {
//        return stepCount
//    }
//
//    fun getTotalDistance(): Float {
//        return totalDistance
//    }
//
//    fun getAverageSpeed(): Float {
//        return speed
//    }
//    fun getLastAltitude(): Double {
//        return altitude
//    }


}