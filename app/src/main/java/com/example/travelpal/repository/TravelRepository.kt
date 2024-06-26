package com.example.travelpal.repository

import android.content.Context
import com.example.travelpal.data.TravelEntity
import com.example.travelpal.database.TravelDatabase
import com.example.travelpal.database.TravelEntityDao

class TravelRepository(
    context: Context,
    private val travelEntityDao: TravelEntityDao = TravelDatabase.create(context).travelEntityDao()
) {
    fun createTravel(travelEntity: TravelEntity): Long {
        return travelEntityDao.persistTravelData(travelEntity)
    }

    fun updateTravel(travelEntity: TravelEntity) {
        travelEntityDao.updateTravelData(travelEntity)
    }

    fun deleteTravel(travelEntity: TravelEntity) {
        travelEntityDao.deleteTravelData(travelEntity)
    }

    fun getAllTravels(): List<TravelEntity> =
        travelEntityDao.selectAllRoutes()

    fun getTravelById(id: Long): TravelEntity =
        travelEntityDao.selectRouteById(id)
}