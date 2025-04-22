package com.example.geobuddy.retrofit

data class ChangePassword(
    val currentPassword: String,
    val newPassword: String,
    val confirmPassword: String
)
