package com.example.travelpal.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class TravelEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val coverUrl: String?,
    val destinationName: String,
    val date: String,
    val description: String,
) : Parcelable
