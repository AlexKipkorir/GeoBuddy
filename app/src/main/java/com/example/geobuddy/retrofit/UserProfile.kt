package com.example.geobuddy.retrofit

import com.google.gson.annotations.SerializedName

data class UserProfile (
    val id: Long,
    val username: String,
    val email: String,
    @SerializedName("phone_number") val phoneNumber: String
)
