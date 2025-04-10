package com.example.geobuddy.retrofit

data class SignupResponse(
    val id: Long,
    val username: String,
    val email: String,
    val phoneNumber: String,
    val password: String,
    val verificationCode: String,
    val verificationCodeExpiresAt: String,
    val enabled: Boolean,
    val authorities: List<Any>,
    val accountNonLocked: Boolean,
    val accountNonExpired: Boolean,
    val credentialsNonExpired: Boolean,


)
