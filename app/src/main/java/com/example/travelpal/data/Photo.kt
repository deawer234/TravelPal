package com.example.travelpal.data

data class Photo(
    val id: Int,
    val travelEntryId: Int,
    val uri: String,
    val description: String?,
    val dateTaken: Long
)
