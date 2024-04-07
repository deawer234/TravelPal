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
)])
@Parcelize
data class Location(
    @PrimaryKey
    val id: Long,
    val travelEntryId: Int,
    val latitude: Double,
    val longitude: Double,
    val name: String?,
    val visitDate: Long
) : Parcelable
