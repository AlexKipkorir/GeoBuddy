package com.example.geobuddy.retrofit

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitService {
//    @POST("ea33cd0f-9310-40c0-a766-8eaddebea162")
//   fun sendOtp(@Body request: OtpRequest): Call<ApiResponse>

    @POST("auth/verify")
   fun verifyOtp(@Body request: OtpVerificationRequest): Call<ApiResponse>

   @POST("auth/login")
   fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @POST("auth/signup")
    fun registerUser(@Body request: SignupRequest): Response<SignupResponse>
}
