package com.example.travelpal.ui.service

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {

    fun getLocationUpdates(interval: Long): Flow<Map<Location, Int>>

    class LocationException(message: String) : Exception(message)
}