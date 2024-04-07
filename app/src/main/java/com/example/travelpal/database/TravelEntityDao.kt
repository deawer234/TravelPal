package com.example.travelpal.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.travelpal.data.TravelEntity

@Dao
interface TravelEntityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun persistRouteData(lyricsData: TravelEntity)

    @Query("SELECT * FROM TravelEntity")
    fun selectAllRoutes(): List<TravelEntity>
}