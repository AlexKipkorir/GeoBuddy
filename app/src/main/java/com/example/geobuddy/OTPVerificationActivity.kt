package com.example.geobuddy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.geobuddy.retrofit.OtpRequest
import com.example.geobuddy.retrofit.OtpResponse
import com.example.geobuddy.retrofit.OtpVerificationRequest
import com.example.geobuddy.retrofit.RetrofitClient
import com.example.geobuddy.retrofit.RetrofitService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OTPVerificationActivity : AppCompatActivity() {
    private lateinit var email: String
    private lateinit var otpInput: EditText
    private lateinit var verifyButton: Button
    private lateinit var resendOtpText: TextView
    private lateinit var apiService: RetrofitService // Dummy Retrofit API

    // Initialize views and set click listeners
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)

        email = intent.getStringExtra("email") ?: ""
        otpInput = findViewById(R.id.otpInput)
        verifyButton = findViewById(R.id.verifyButton)
        resendOtpText = findViewById(R.id.resendOtpText)

        apiService = RetrofitClient.retrofitService

        verifyButton.setOnClickListener {
            val otp = otpInput.text.toString().trim()

            if (otp.length != 6) {
                Toast.makeText(this, "Please enter a valid OTP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            verifyOTP(email, otp)
        }
        resendOtpText.setOnClickListener {
            sendOTP(email)
        }
    }

    // Function to send OTP
    private fun sendOTP(email: String) {
        val request = OtpRequest(email)
        val call = apiService.sendOtp(request)

        call.enqueue(object : Callback<OtpResponse> {
            // Handle successful response
            override fun onResponse(call: Call<OtpResponse>, response: Response<OtpResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@OTPVerificationActivity, "OTP sent successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@OTPVerificationActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
            // Handle failure
            override fun onFailure(call: Call<OtpResponse>, t: Throwable) {
                val errorMessage = "Failed to send OTP: ${t.message}"
                Toast.makeText(this@OTPVerificationActivity, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("OTPVerification", errorMessage, t)  // Logs error to Logcat
            }

        })
    }

    // Function to verify OTP
    private fun verifyOTP(email: String, verificationCode: String) {
        val request = OtpVerificationRequest(email, verificationCode) // Create a verification request object
        val call = apiService.verifyOtp(request)

        call.enqueue(object : Callback<ResponseBody> {
            // Handle successful response
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                Log.d("OTP_RESPONSE", "Code: ${response.code()}")
                Log.d("OTP_RESPONSE", "Body: ${response.body()}")
                if (response.isSuccessful) {
                    val message = response.body()?.string() ?: "No response"
                    Toast.makeText(this@OTPVerificationActivity, message, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@OTPVerificationActivity, DashboardActivity::class.java))
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("OTP_ERROR", "Failed to verify OTP: $errorBody")
                    Toast.makeText(this@OTPVerificationActivity, "Invalid OTP", Toast.LENGTH_SHORT).show()
                }

            }

            // Handle failure
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("OTP_ERROR", "Failed to verify OTP: ${t.message}", t)
                Toast.makeText(this@OTPVerificationActivity, "Verification Failed", Toast.LENGTH_SHORT).show()
            }
        })
    }


}