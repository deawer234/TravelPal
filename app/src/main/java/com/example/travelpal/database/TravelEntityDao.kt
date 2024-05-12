package com.example.travelpal.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.travelpal.data.TravelEntity

@Dao
interface TravelEntityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun persistTravelData(travelEntity: TravelEntity): Long

    @Delete()
    fun deleteTravelData(travelEntity: TravelEntity)

    @Update
    fun updateTravelData(travelEntity: TravelEntity)

    @Query("SELECT * FROM TravelEntity")
    fun selectAllRoutes(): List<TravelEntity>

    @Query("SELECT * FROM TravelEntity WHERE id = :id")
    fun selectRouteById(id: Long): TravelEntity
}