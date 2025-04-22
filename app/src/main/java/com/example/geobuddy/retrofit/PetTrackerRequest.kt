package com.example.geobuddy.retrofit

data class PetTrackerRequest (
    val trackerName: String,
    val imei: String,
    val batteryCapacity: Double = 100.0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val status: String = "active",
    val breed: String,
    val age: Int,
    val description: String
)
