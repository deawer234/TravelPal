package com.example.travelpal.repository

import android.content.Context
import com.example.travelpal.data.TravelEntity
import com.example.travelpal.database.TravelEntityDao
import com.example.travelpal.database.TravelDatabase

class TravelRepository (
    context: Context,
    private val travelEntityDao: TravelEntityDao = TravelDatabase.create(context).travelEntityDao()
) {
    fun createTravel(destinationName: String, date: String, description: String){
        val newTravel = TravelEntity(
            destinationName = destinationName,
            date = date,
            description = description,
            coverUrl = null
        )
        travelEntityDao.persistTravelData(newTravel)
    }

    fun deleteTravel(travelEntity: TravelEntity){
        travelEntityDao.deleteTravelData(travelEntity)
    }
    fun getAllTravels(): List<TravelEntity> =
        travelEntityDao.selectAllRoutes()
}