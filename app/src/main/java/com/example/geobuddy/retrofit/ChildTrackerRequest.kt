package com.example.geobuddy.retrofit

data class ChildTrackerRequest(
    val trackerName: String,
    val imei: String,
    val age: Int,
    val description: String,
    val name: String,
    val status: String = "active",
    val batteryCapacity: Double = 100.0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
