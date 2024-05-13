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
    var mapThumbnail: ByteArray?,
    val destinationName: String,
    val date: String,
    val description: String,
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TravelEntity

        if (id != other.id) return false
        if (mapThumbnail != null) {
            if (other.mapThumbnail == null) return false
            if (!mapThumbnail.contentEquals(other.mapThumbnail)) return false
        } else if (other.mapThumbnail != null) return false
        if (destinationName != other.destinationName) return false
        if (date != other.date) return false
        return description == other.description
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (mapThumbnail?.contentHashCode() ?: 0)
        result = 31 * result + destinationName.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + description.hashCode()
        return result
    }
}
