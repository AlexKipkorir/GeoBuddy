package com.example.geobuddy.retrofit

data class TrackerImeiResponse(
    val id: Long,
    val trackerName: String,
    val imei: String,
    val batteryCapacity: Double,
    val latitude: Double,
    val longitude: Double,
    val status: String,
    val lastUpdated: String
)
