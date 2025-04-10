package com.example.geobuddy.retrofit

data class OtpVerificationRequest(
    val email: String,
    val verificationCode: String
)
