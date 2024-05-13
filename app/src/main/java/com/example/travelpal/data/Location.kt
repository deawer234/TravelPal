package com.example.travelpal.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(
    foreignKeys = [ForeignKey(
        entity = TravelEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("travelEntryId"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["travelEntryId"])]
)
@Parcelize
data class Location(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val travelEntryId: Long,
    val latitude: Double,
    val longitude: Double,
    val visitDate: String,
    val steps: Int,
    val elevation: Double,
    val traveled: Float,
    val speed: Float
) : Parcelable
