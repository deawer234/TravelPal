package com.example.travelpal.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Entity
@Parcelize
data class TravelEntity(
    @PrimaryKey
    val id: Long,
    val destinationName: String,
    val date: String,
    val description: String,
    val photos: @RawValue List<Photo>,
    val locations: @RawValue List<Location>
) : Parcelable
