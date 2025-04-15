package com.example.geobuddy.retrofit

data class TrackerRequest(
    val userId: String,
    val trackerName: String,
    val imei: String,
    val trackerType: String,
    val imageUrl: String,
    val trackerStatus: String,
    val latitude: Double,
    val longitude: Double,
    val battery: String
)
