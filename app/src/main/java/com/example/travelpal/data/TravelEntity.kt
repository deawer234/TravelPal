package com.example.travelpal.data

data class TravelEntity(
    val id: Int,
    val destinationName: String,
    val date: Long,
    val description: String,
    val photos: List<Photo>,
    val locations: List<Location>
)
