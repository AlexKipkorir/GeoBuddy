package com.example.geobuddy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.geobuddy.retrofit.LoginRequest
import com.example.geobuddy.retrofit.LoginResponse
import com.example.geobuddy.retrofit.RetrofitClient
import com.example.geobuddy.retrofit.RetrofitService
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {
//    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Authentication
//        auth = FirebaseAuth.getInstance()

        val emailInput: EditText = findViewById(R.id.emailInput)
        val passwordInput: EditText = findViewById(R.id.passwordInput)
        val signInButton: Button = findViewById(R.id.signInButton)
        val googleSignInButton: ImageView = findViewById(R.id.googleSignInButton)
        val signUpText: TextView = findViewById(R.id.signUpText)
        val forgotPasswordText: TextView = findViewById(R.id.forgotPasswordText)
        val passwordToggle: ImageView = findViewById(R.id.passwordToggle)
        var isPasswordVisible = false

        // Sign In Button Click Listener
        signInButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

           if (email.isEmpty() || password.isEmpty()) {
               Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
               return@setOnClickListener
           }
//            signInUser(email, password)
            loginUser(email, password)
        }
        // Google Sign In Button Click Listener
        googleSignInButton.setOnClickListener {
            //TODO: Call Google Sign In API
        }
        // Sign Up Text Click Listener
        signUpText.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
        //Navigate to Forgot Password Activity
        forgotPasswordText.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        passwordToggle.setOnClickListener {
            if (isPasswordVisible) {
                passwordInput.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                passwordToggle.setImageResource(R.drawable.ic_eye)
            } else {
                passwordInput.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                passwordToggle.setImageResource(R.drawable.ic_eye)
            }
            passwordInput.setSelection(passwordInput.text.length)
            isPasswordVisible = !isPasswordVisible
        }
    }

    //Function to sign in user
//    Firebase
//    private fun signInUser(email: String,password: String) {
//        auth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener(this) { task ->
//                if(task.isSuccessful) {
//                    Toast.makeText(this, "Sign in Successful!", Toast.LENGTH_SHORT).show()
//                    startActivity(Intent(this, DashboardActivity::class.java))
//                    finish()
//                } else {
//                    Toast.makeText(this, "Sign in Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }

    //Retrofit
    private fun loginUser(email: String, password: String) {
        val request = LoginRequest(email, password)

        val service = RetrofitClient.retrofitService
        service.loginUser(request).enqueue(object : Callback<LoginResponse> {

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    val token = loginResponse.response
                    val expiry = loginResponse.expiresIn

                    //Save token and expiry to shared preferences
                    val prefs = getSharedPreferences("login_prefs", MODE_PRIVATE)
                    val rawToken = loginResponse.response.replace("Bearer ", "")
                    with(prefs.edit()) {
                        putString("jwt_token", rawToken)
                        putLong("token_expiry", System.currentTimeMillis() + expiry)
                        apply()
                    }

                    Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                    Log.d("LOGIN_SUCCESS", "Token received: $token")
                    val savedToken = prefs.getString("jwt_token", "")
                    Log.d("STORED_TOKEN", "Saved token: $savedToken")
                    startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Login Failed: Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Login Failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}