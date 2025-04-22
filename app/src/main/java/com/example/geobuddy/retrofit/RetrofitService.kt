package com.example.geobuddy.retrofit

import com.example.geobuddy.models.Tracker
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Field
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PUT
import retrofit2.http.Path

interface RetrofitService {

    //OTP
    @POST("auth/resend")
   fun sendOtp(@Body request: OtpRequest): Call<OtpResponse>

    @POST("auth/verify")
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
   fun verifyOtp(@Body request: OtpVerificationRequest): Call<OtpResponse>

   //Authentication
   @POST("auth/login")
   fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @POST("auth/signup")
    fun signUpUser(@Body request: SignupRequest): Call<SignupResponse>

    //Display User Details and Delete Account
    @GET("users/me")
    fun getUserDetails(
        @Header("Authorization") authHeader: String
    ): Call<UserProfile>

    @DELETE("users/me")
    fun deleteAccount(
        @Header("Authorization") authHeader: String
    ): Call<ApiResponse>

    //Update Password
    @PUT("users/me/password")
    fun updatePassword(
        @Header("Authorization") authHeader: String,
        @Body changePasswordDto: ChangePassword
    ): Call<ApiResponse>

    //Register and Retrieve Trackers
    @GET("users/trackers")
    fun getTrackers(
        @Header("Authorization") authHeader: String
    ): Call<List<Tracker>>

    //Trackers
    @GET("users/trackers/data/{imei}")
    fun getTrackerByImei(
        @Header("Authorization") authHeader: String,
        @Path("imei") imei: String
    ): Call<TrackerImeiResponse>

    @POST("users/trackers/pet")
    fun registerPetTracker(
        @Header("Authorization") token: String,
        @Body request: PetTrackerRequest
    ): Call<Void>

    @POST("users/trackers/luggage")
    fun registerLuggageTracker(
        @Header("Authorization") token: String,
        @Body request: LuggageTrackerRequest
    ): Call<Void>

    @POST("users/trackers/child")
    fun registerChildTracker(
        @Header("Authorization") token: String,
        @Body request: ChildTrackerRequest
    ): Call<Void>

    //Tracker Count
//    @GET("/trackers/count")
//    fun getTrackerCount(@Header("Authorization") token: String): Call<Int>
//
}
