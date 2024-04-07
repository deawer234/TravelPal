package com.example.travelpal.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize



@Entity(foreignKeys = [ForeignKey(
    entity = TravelEntity::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("travelEntryId"),
    onDelete = ForeignKey.CASCADE
)],
    //indices = [Index(value = ["travelEntryId"])]
)

@Parcelize
data class Location(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val travelEntryId: Long,
    val latitude: Double,
    val longitude: Double,
    val name: String?,
    val visitDate: Long
) : Parcelable
