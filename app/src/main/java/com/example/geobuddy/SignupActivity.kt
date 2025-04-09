package com.example.geobuddy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.geobuddy.retrofit.RetrofitClient
import com.example.geobuddy.retrofit.RetrofitService
import com.example.geobuddy.retrofit.SignupRequest
import com.example.geobuddy.retrofit.SignupResponse
import retrofit2.Call
import retrofit2.Callback

class SignupActivity : AppCompatActivity() {

    // private lateinit var auth: FirebaseAuth

    //Initialize Views
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        //Initialize Firebase Authentication
        //auth = FirebaseAuth.getInstance()

        val usernameInput = findViewById<EditText>(R.id.usernameInput)
        val phoneNoInput = findViewById<EditText>(R.id.phoneNumberInput)
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val confirmPasswordInput = findViewById<EditText>(R.id.confirmPasswordInput)
        val termsCheckbox = findViewById<CheckBox>(R.id.termsCheckbox)
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        val googleSignUpButton = findViewById<ImageView>(R.id.googleSignUpButton)
        val signInText = findViewById<TextView>(R.id.signInText)


        //Enable Sign-Up Button only if all fields are filled and terms are accepted
        termsCheckbox.setOnCheckedChangeListener { _, isChecked ->
            signUpButton.isEnabled = isChecked
        }

        //Sign Up Button Click Listener
        signUpButton.setOnClickListener{
            val username = usernameInput.text.toString()
            val phoneNumber = phoneNoInput.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString().trim()

            if(username.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
                if (password == confirmPassword) {
                    signUpUser(username, phoneNumber, email, password)
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }

        }

        //Google Sign Up Button Click Listener
        googleSignUpButton.setOnClickListener{
            //TODO: Call Google Sign Up API
        }

        //Navigate to Login Page
        signInText.setOnClickListener{
            val intent = Intent(this, LoginActivity ::class.java)
            intent.putExtra("email", emailInput.text.toString())
            startActivity(intent)
            finish()
        }

    }

    private fun signUpUser(username: String, phoneNumber: String, email: String, password: String) {
        val request = SignupRequest(username,phoneNumber, email, password)

        val service = RetrofitClient.instance.create(RetrofitService::class.java)
        service.signUpUser(request).enqueue(object : Callback<SignupResponse> {

            override fun onResponse(
                call: Call<SignupResponse>,
                response: retrofit2.Response<SignupResponse>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@SignupActivity, "Sign up successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@SignupActivity, OTPVerificationActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@SignupActivity, "Sign up failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                Toast.makeText(this@SignupActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }
}
/*
    //Function to sign up user
    private fun signUpUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Signup Successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, OTPVerificationActivity ::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Signup Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
*/




/*

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize views using findViewById
        val buttonSignup: Button = findViewById(R.id.signUpButton)
        val usernameInput: EditText = findViewById(R.id.usernameInput)
        val emailInput: EditText = findViewById(R.id.emailInput)
        val passwordInput: EditText = findViewById(R.id.passwordInput)

        buttonSignup.setOnClickListener {
            Log.d("SignupActivity", "Sign up button clicked")
            Toast.makeText(this, "Button Clicked", Toast.LENGTH_SHORT).show()

            val username = usernameInput.text.toString().trim()
            val phoneNo = Input.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            // Optional validation
            if (username.isNotEmpty() && phoneNo.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                registerUser(username, phoneNo, email, password)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

//        signUpButton.setOnClickListener {
//            val username = usernameInput.text.toString().trim()
//            val email = emailInput.text.toString().trim()
//            val password = passwordInput.text.toString().trim()
//
//            // Validate inputs
//            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
//                registerUser(username, email, password)
//            } else {
//                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
//            }
//        }
    }

    private fun registerUser(username: String, email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Call the signup endpoint via Retrofit
                val response = RetrofitClient.apiService.registerUser(
                    SignupRequest(username, email, password)
                )
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.message != null) {
                        Toast.makeText(
                            this@SignupActivity,
                            "Signup successful: ${response.body()?.message}",
                            Toast.LENGTH_SHORT

                        ).show()
                        startActivity(Intent(this@SignupActivity, OTPVerificationActivity::class.java))
                        finish()

                    } else {
                        Toast.makeText(
                            this@SignupActivity,
                            "Signup failed: ${response.body()?.error ?: "Unknown error"}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@SignupActivity,
                        "Exception: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Log.e("Signup", "Exception: ${e.message}")
            }
        }
    }
}

*/