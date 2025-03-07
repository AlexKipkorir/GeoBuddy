package com.example.geobuddy

import com.example.geobuddy.ApiResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitService {
    @POST("send_otp")
   fun sendOtp(@Body request: OtpRequest): Call<ApiResponse>

    @POST("verify_otp")
   fun verifyOtp(@Body request: OtpVerificationRequest): Call<ApiResponse>
}
