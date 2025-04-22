package com.example.geobuddy

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.geobuddy.retrofit.ApiResponse
import com.example.geobuddy.retrofit.ChangePassword
import com.example.geobuddy.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ModifyPasswordActivity : AppCompatActivity() {

    private lateinit var oldPasswordEditText: EditText
    private lateinit var newPasswordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText

    private lateinit var oldPassToggle: ImageView
    private lateinit var newPassToggle: ImageView
    private lateinit var confirmPassToggle: ImageView

    private lateinit var confirmButton: Button

    private var isOldPasswordVisible = false
    private var isNewPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_password)

        // Initialize views
        oldPasswordEditText = findViewById(R.id.etOldPassword)
        newPasswordEditText = findViewById(R.id.etNewPassword)
        confirmPasswordEditText = findViewById(R.id.etConfirmPassword)

        oldPassToggle = findViewById(R.id.ivOldPassToggle)
        newPassToggle = findViewById(R.id.ivNewPassToggle)
        confirmPassToggle = findViewById(R.id.ivConfirmPassToggle)

        confirmButton = findViewById(R.id.btnConfirm)

        // Toggle password visibility
        oldPassToggle.setOnClickListener {
            isOldPasswordVisible = !isOldPasswordVisible
            togglePasswordVisibility(oldPasswordEditText, isOldPasswordVisible)
        }

        newPassToggle.setOnClickListener {
            isNewPasswordVisible = !isNewPasswordVisible
            togglePasswordVisibility(newPasswordEditText, isNewPasswordVisible)
        }

        confirmPassToggle.setOnClickListener {
            isConfirmPasswordVisible = !isConfirmPasswordVisible
            togglePasswordVisibility(confirmPasswordEditText, isConfirmPasswordVisible)
        }

        // Confirm button logic
        confirmButton.setOnClickListener {
            val oldPassword = oldPasswordEditText.text.toString().trim()
            val newPassword = newPasswordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (oldPassword.length !in 6..12 || newPassword.length !in 6..12 || confirmPassword.length !in 6..12) {
                Toast.makeText(
                    this,
                    "Password must be between 6 and 12 characters",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prefs = getSharedPreferences("login_prefs", MODE_PRIVATE)
            val token = prefs.getString("jwt_token", "") ?: ""

            val changeRequest = ChangePassword(
                currentPassword = oldPassword,
                newPassword = newPassword,
                confirmPassword = confirmPassword
            )

            RetrofitClient.retrofitService.updatePassword("Bearer $token", changeRequest)
                .enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(
                        call: Call<ApiResponse>,
                        response: Response<ApiResponse>
                    ) {
                        val apiResponse = response.body()
                        if (response.isSuccessful && apiResponse?.success == true) {
                            Toast.makeText(
                                applicationContext,
                                "Password updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Clear login info and redirect
                            val prefs = getSharedPreferences("login_prefs", MODE_PRIVATE)
                            prefs.edit().clear().apply()

                            startActivity(
                                Intent(
                                    this@ModifyPasswordActivity,
                                    LoginActivity::class.java
                                )
                            )
                            finish()

                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Update failed: ${apiResponse?.message ?: "Unknown error"}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        Toast.makeText(
                            applicationContext,
                            "Network error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }

    private fun togglePasswordVisibility(editText: EditText, isVisible: Boolean) {
        editText.inputType =
            if (isVisible) InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        editText.setSelection(editText.text.length)
    }
}
