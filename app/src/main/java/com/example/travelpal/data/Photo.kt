package com.example.travelpal.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(foreignKeys = [ForeignKey(
    entity = TravelEntity::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("travelEntryId"),
    onDelete = ForeignKey.CASCADE
)],
    indices = [Index(value = ["travelEntryId"])]
)
@Parcelize
data class Photo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val travelEntryId: Long,
    val uri: String,
    val description: String?,
    val dateTaken: Long
) : Parcelable
