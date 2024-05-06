package com.example.travelpal.ui.service

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import hasLocationPermission
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class LocationClientImpl(
    private val context: Context,
    private val client: FusedLocationProviderClient
) : LocationClient {

    private lateinit var sensorManager: SensorManager
    private var stepDetectorSensor: Sensor? = null
    private lateinit var sensorEventListener: SensorEventListener
    private var stepCount = 0
    //private val data = mutableMapOf<Location, Int>()

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Map<Location, Int>> {
        return callbackFlow {
            if (!context.hasLocationPermission()) {
                throw LocationClient.LocationException("Location permission not granted")
            }

            val locationManager: LocationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGpsEnabled && !isNetworkEnabled) {
                throw LocationClient.LocationException("GPS is disabled")
            }

            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setWaitForAccurateLocation(true)
                .setMinUpdateIntervalMillis(interval - interval / 2)
                .setMaxUpdateDelayMillis(interval + interval / 2)
                .build()

            startStepCounter()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    locationResult.locations.lastOrNull()?.let {
                        val data = mapOf(it to stepCount)
                        launch { send(data) }
                    }
                }
            }

            client.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )

            awaitClose {
                stopStepCounter()
                client.removeLocationUpdates(locationCallback)
            }
        }
    }

    private fun startStepCounter() {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

        sensorEventListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                // You can implement this method if you want to react to changes in the sensor's accuracy
            }

            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor == stepDetectorSensor) {
                    stepCount += event.values[0].toInt()
                    // Do something with the step count
                    // For example, you can save it to your database or display it in your UI
                }
            }
        }
        sensorManager.registerListener(
            sensorEventListener,
            stepDetectorSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    private fun stopStepCounter() {
        sensorManager.unregisterListener(sensorEventListener)
    }
}
