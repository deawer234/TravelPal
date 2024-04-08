package com.example.travelpal.ui.manager

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date

class LiveTrackingManager(private val context: Context)  {
    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private lateinit var locationCallback: LocationCallback
    private var locationRequest: LocationRequest? = null

    private val locationRepository: LocationRepository by lazy {
        LocationRepository(context)
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create()?.apply {
            interval = 60000 // 1 minute interval
            fastestInterval = 30000 // 30 seconds
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(travelEntityId: Long) {
        createLocationRequest()
        locationCallback = object : LocationCallback() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    // Save location data
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
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
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
}