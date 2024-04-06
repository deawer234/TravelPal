package com.example.travelpal.data

data class Location(
    val id: Int,
    val travelEntryId: Int,
    val latitude: Double,
    val longitude: Double,
    val name: String?,
    val visitDate: Long
)
