package com.example.geobuddy.retrofit

import com.example.geobuddy.models.Tracker
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Field
import retrofit2.http.Header
import retrofit2.http.Headers
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

    @GET("trackers")
    fun getTrackers(@Query("userId") userId: String): Call<List<Tracker>>

    //Trackers
    @GET("trackers/{imei}")
    fun getTrackerByImei(@Path("imei") imei: String): Call<TrackerImeiResponse>

    @POST("trackers/pets")
    fun registerPetTracker(
//        @Header("Authorization") token: String,
        @Body request: PetTrackerRequest
    ): Call<Void>

    @POST("trackers/luggage")
    fun registerLuggageTracker(
//        @Header("Authorization") token: String,
        @Body request: LuggageTrackerRequest
    ): Call<Void>

    @POST("trackers/children")
    fun registerChildTracker(
//        @Header("Authorization") token: String,
        @Body request: ChildTrackerRequest
    ): Call<Void>

}
