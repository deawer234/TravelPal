package com.example.travelpal.repository

import android.content.Context
import com.example.travelpal.data.Photo
import com.example.travelpal.database.TravelDatabase
import com.example.travelpal.database.PhotoDao

class PhotoRepository (
    context: Context,
    private val photoDao: PhotoDao = TravelDatabase.create(context).photoDao()
) {

    fun getAllPhotosForTravel(travelEntityId: Long): List<Photo> =
        photoDao.selectPhotosForTravelEntry(travelEntityId)
}