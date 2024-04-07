package com.example.travelpal.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.travelpal.data.Location
import com.example.travelpal.data.Photo
import com.example.travelpal.data.TravelEntity

@Database(
    entities = [TravelEntity::class, Photo::class, Location::class],
    version = 2
)
abstract class TravelDatabase : RoomDatabase() {
    companion object {
        private const val NAME = "travel_pal.db"

        fun create(context: Context): TravelDatabase =
            Room.databaseBuilder(context, TravelDatabase::class.java, NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
    }

    abstract fun travelEntityDao(): TravelEntityDao

    abstract fun photoDao(): PhotoDao
}