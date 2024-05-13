package com.example.travelpal.ui.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
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
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class TrackerService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private lateinit var stepCounter: StepCounter
    private lateinit var locationClient: LocationClient
    private var notification: NotificationCompat.Builder? = null

    private var totalDistance = 0f
    private var startTime = 0L
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timeUpdateRunnable: Runnable



    private var travelEntityId = -1L

    private lateinit var lastLocation: Location
    private var startingLocation: Location? = null


    val locationsData = MutableLiveData<Location>()

    private val binder = LocalBinder()
    inner class LocalBinder : Binder() {
        fun getService(): TrackerService = this@TrackerService
    }

    val timeRunInSeconds = MutableLiveData<Long>()
    val stepCountData = MutableLiveData<Int>()
    val totalDistanceData = MutableLiveData<Float>()
    val averageSpeedData = MutableLiveData<Float>()
    val lastAltitudeData = MutableLiveData<Double>()

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
        stepCounter = StepCounter(applicationContext)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        travelEntityId = intent?.getLongExtra("travelEntityId", -1L) ?: -1L
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startTimer(){
        startTime = System.currentTimeMillis()
        CoroutineScope(Dispatchers.Main).launch {

        }
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

        startTime = System.currentTimeMillis()

        timeUpdateRunnable = Runnable {
            val elapsedTime = getElapsedTimeInSeconds()
            timeRunInSeconds.postValue(elapsedTime)
            // Update the UI with the formatted time
            // Replace "timeTextView" with the ID of your TextView

            // Post the Runnable again with a delay of 1 second
            handler.postDelayed(timeUpdateRunnable, 1000)
        }

        handler.post(timeUpdateRunnable)

        stepCounter.startTrackingSteps()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

        locationClient
            .getLocationUpdates(5000L)
            .catch { e -> e.printStackTrace() }
            .onEach {
                if (startingLocation == null) {
                    startingLocation = it
                    lastLocation = it
                }
                stepCountData.postValue(stepCounter.getStepCount())

                locationsData.postValue(it)

                totalDistance += it.distanceTo(lastLocation)
                totalDistanceData.postValue(totalDistance)

                averageSpeedData.postValue(it.speed)

                lastAltitudeData.postValue(it.altitude)

                lastLocation = it

                saveLocationPoint(it, travelEntityId)
                notification?.setContentText("Location: ${it.latitude}, ${it.longitude}")
                notificationManager.notify(1, notification?.build())
            }
            .launchIn(serviceScope)

        startForeground(1, notification?.build())

    }

    private fun stop() {
        stopForeground(true)
        handler.removeCallbacks(timeUpdateRunnable)
        stopSelf()
    }

    private fun getElapsedTimeInSeconds(): Long {
        val elapsedTimeMillis = System.currentTimeMillis() - startTime
        return elapsedTimeMillis / 1000
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
            elevation = location.altitude,
            traveled = totalDistance,
            speed = location.speed,
            steps = stepCountData.value ?: 0
        )
        locationRepository.createLocation(newLocation)
        println(newLocation)
    }

}