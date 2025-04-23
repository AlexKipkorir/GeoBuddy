package com.example.geobuddy.models

data class Tracker (
    val imei : String,
    val trackerName: String,
    val type: String,
    val description: String,
    val status: String,
    val batteryCapacity: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: String,
    val age: String?,
    val breed: String?,
    val luggageType: String?,
    val color: String?
)
