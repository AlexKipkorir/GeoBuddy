package com.example.geobuddy

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.geobuddy.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.geobuddy.OtpRequest
import com.example.geobuddy.OtpVerificationRequest
import com.example.geobuddy.RetrofitService
import com.example.geobuddy.ApiResponse

class OTPVerificationActivity : AppCompatActivity() {
    private lateinit var email: String
    private lateinit var otpInput: EditText
    private lateinit var verifyButton: Button
    private lateinit var resendOtpText: TextView
    private lateinit var apiService: RetrofitService // Dummy Retrofit API

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)

        email = intent.getStringExtra("email") ?: ""
        otpInput = findViewById(R.id.otpInput)
        verifyButton = findViewById(R.id.verifyButton)
        resendOtpText = findViewById(R.id.resendOtpText)

        apiService = RetrofitClient.instance.create(RetrofitService::class.java)

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
    private fun sendOTP(email: String) {
        val request = OtpRequest(email) // Create a request object
        val call = apiService.sendOtp(request)
        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                Toast.makeText(this@OTPVerificationActivity, "OTP sent successfully", Toast.LENGTH_SHORT).show()
            }
            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(this@OTPVerificationActivity, "Failed to send OTP", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun verifyOTP(email: String, otp: String) {
        val request = OtpVerificationRequest(email, otp) // Create a verification request object
        val call = apiService.verifyOtp(request)

        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@OTPVerificationActivity, "OTP Verified", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@OTPVerificationActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@OTPVerificationActivity, "Invalid OTP", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(this@OTPVerificationActivity, "Verification Failed", Toast.LENGTH_SHORT).show()
            }
        })
    }


}