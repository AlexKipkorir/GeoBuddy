package com.example.geobuddy.retrofit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitService {
    @POST("auth/resend")
   fun sendOtp(@Body request: OtpRequest): Call<ApiResponse>

    @POST("auth/verify")
   fun verifyOtp(@Body request: OtpVerificationRequest): Call<ApiResponse>

   @POST("auth/login")
   fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @POST("auth/signup")
    fun signUpUser(@Body request: SignupRequest): Call<SignupResponse>
}
