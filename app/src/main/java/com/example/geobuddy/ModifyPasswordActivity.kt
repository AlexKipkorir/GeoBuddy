package com.example.geobuddy

import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.geobuddy.retrofit.ApiResponse
import com.example.geobuddy.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ModifyPasswordActivity : AppCompatActivity()  {

    private lateinit var mobileEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var oldPasswordEditText: EditText
    private lateinit var newPasswordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText

    private lateinit var oldPassToggle: ImageView
    private lateinit var newPassToggle: ImageView
    private lateinit var confirmPassToggle: ImageView

    private lateinit var confirmButton: Button

    private var isOldPasswordVisible  = false
    private var isNewPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_password)

        //Initialize views
        mobileEditText = findViewById(R.id.etMobile)
        emailEditText = findViewById(R.id.etEmail)
        oldPasswordEditText = findViewById(R.id.etOldPassword)
        newPasswordEditText = findViewById(R.id.etNewPassword)
        confirmPasswordEditText = findViewById(R.id.etConfirmPassword)

        oldPassToggle = findViewById(R.id.ivOldPassToggle)
        newPassToggle = findViewById(R.id.ivNewPassToggle)
        confirmPassToggle = findViewById(R.id.ivConfirmPassToggle)

        confirmButton = findViewById(R.id.btnConfirm)

        //Toggle password visibility
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
            val mobile = mobileEditText.text.toString()
            val email = emailEditText.text.toString()
            val oldPassword = oldPasswordEditText.text.toString()
            val newPassword = newPasswordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (mobile.isEmpty() || email.isEmpty() || oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
             Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
             return@setOnClickListener
            }

            if (oldPassword.length !in 6..12 || newPassword.length !in 6..12 || confirmPassword.length !in 6..12) {
                Toast.makeText(this, "Password must be between 6 and 12 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val prefs = getSharedPreferences("login_prefs", MODE_PRIVATE)
            val token = prefs.getString("jwt_token", "") ?: ""

            RetrofitClient.retrofitService.validateUser(mobile, email, oldPassword)
                .enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            // Call update password
                            RetrofitClient.retrofitService.updatePassword(token, mobile, newPassword)
                                .enqueue(object : Callback<ApiResponse> {
                                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                                        if (response.isSuccessful && response.body()?.success == true) {
                                            Toast.makeText(applicationContext, "Password updated successfully", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(applicationContext, "Update failed", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                                        Toast.makeText(applicationContext, "Update error: ${t.message}", Toast.LENGTH_SHORT).show()
                                    }
                                })
                        } else {
                            Toast.makeText(applicationContext, "Validation failed", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, "Validation error: ${t.message}", Toast.LENGTH_SHORT).show()
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
