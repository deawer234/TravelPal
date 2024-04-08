package com.example.travelpal.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.travelpal.data.Location

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun persistLocationData(location: Location): Long

    @Query("SELECT * FROM Location WHERE travelEntryId = :travelEntryId")
    fun getLocationsForTravelEntry(travelEntryId: Long): List<Location>
}