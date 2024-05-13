package com.example.travelpal.repository

import android.content.Context
import com.example.travelpal.data.Photo
import com.example.travelpal.database.PhotoDao
import com.example.travelpal.database.TravelDatabase

class PhotoRepository(
    context: Context,
    private val photoDao: PhotoDao = TravelDatabase.create(context).photoDao()
) {

    fun getAllPhotosForTravel(travelEntityId: Long): List<Photo> =
        photoDao.selectPhotosForTravelEntry(travelEntityId)

    fun insertPhotos(photos: List<Photo>) {
        photos.forEach { photoDao.persistPhotoData(it) }
    }

    fun deletePhoto(photo: Photo) {
        photoDao.deletePhoto(photo)
    }
}