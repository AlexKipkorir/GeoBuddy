package com.example.geobuddy.models

data class Tracker (
    val trackerName: String,
    val status: String,
    val battery: String,
    val latitude: Double,
    val longitude: Double
)