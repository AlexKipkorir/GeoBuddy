package com.example.geobuddy

import com.example.geobuddy.ApiResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitService {
    @POST("ea33cd0f-9310-40c0-a766-8eaddebea162")
   fun sendOtp(@Body request: OtpRequest): Call<ApiResponse>

    @POST("413ecae1-c537-4606-a732-a856acd6686a")
   fun verifyOtp(@Body request: OtpVerificationRequest): Call<ApiResponse>
}
