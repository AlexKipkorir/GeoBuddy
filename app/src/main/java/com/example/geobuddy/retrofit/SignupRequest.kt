package com.example.geobuddy.retrofit


data class SignupRequest(
    val username: String,
    val phoneNumber: String,
    val email: String,
    val password: String
)
