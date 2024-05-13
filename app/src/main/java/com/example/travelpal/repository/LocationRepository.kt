package com.example.travelpal.repository

import android.content.Context
import com.example.travelpal.data.Location
import com.example.travelpal.database.LocationDao
import com.example.travelpal.database.TravelDatabase

class LocationRepository(
    context: Context,
    private val locationDao: LocationDao = TravelDatabase.create(context).locationDao()
) {
    fun createLocation(location: Location) {
        locationDao.persistLocationData(location)
    }

    fun getAllTravelLocations(travelEntryId: Long): List<Location> =
        locationDao.getLocationsForTravelEntry(travelEntryId)
}