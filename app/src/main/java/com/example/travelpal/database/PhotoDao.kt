package com.example.travelpal.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.travelpal.data.Photo


@Dao
interface PhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun persistPhotoData(photo: Photo): Long

    @Query("SELECT * FROM Photo WHERE travelEntryId = :travelEntryId")
    fun selectPhotosForTravelEntry(travelEntryId: Long): List<Photo>

    @Delete
    fun deletePhoto(photo: Photo)
}
