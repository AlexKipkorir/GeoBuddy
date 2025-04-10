package com.example.geobuddy.retrofit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Field
import retrofit2.http.Header
import retrofit2.http.Headers

interface RetrofitService {
    @POST("auth/resend")
   fun sendOtp(@Body request: OtpRequest): Call<OtpResponse>

    @POST("auth/verify")
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
   fun verifyOtp(@Body request: OtpVerificationRequest): Call<ResponseBody>

   @POST("auth/login")
   fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @POST("auth/signup")
    fun signUpUser(@Body request: SignupRequest): Call<SignupResponse>

    //GET method to verify user details
    @GET("users/me")
    fun getUserDetails(
        @Header("Authorization") authHeader: String
    ): Call<UserProfile>

    @GET("users/me")
    fun validateUser(
        @Query("mobile") mobile: String,
        @Query("email") email: String,
        @Query("oldPassword") oldPassword: String
    ): Call<ApiResponse>
    //POST method to update user details
    @FormUrlEncoded
    @POST("users/me")
    fun updatePassword(
        @Field("mobile") mobile: String,
        @Field("newPassword") newPassword: String
    ): Call<ApiResponse>
}
