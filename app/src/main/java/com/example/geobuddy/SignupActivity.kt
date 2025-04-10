package com.example.geobuddy

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
        val passwordToggle: ImageView = findViewById(R.id.passwordToggle)
        val confirmPasswordToggle = findViewById<ImageView>(R.id.confirmPasswordToggle)
        var isPasswordVisible = false


        //Enable Sign-Up Button only if all fields are filled and terms are accepted
        termsCheckbox.setOnCheckedChangeListener { _, isChecked ->
            signUpButton.isEnabled = isChecked
        }

        //Sign Up Button Click Listener
        signUpButton.setOnClickListener{
            val username = usernameInput.text.toString().trim()
            val phoneNumber = phoneNoInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()

            if(username.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
                if (password.length < 6) {
                    Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (password == confirmPassword) {

                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            signUpUser(username,email, password, phoneNumber)
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

        confirmPasswordToggle.setOnClickListener{
            if (isPasswordVisible) {
                confirmPasswordInput.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                confirmPasswordToggle.setImageResource(R.drawable.ic_eye)
            } else {
                confirmPasswordInput.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                confirmPasswordToggle.setImageResource(R.drawable.ic_eye)
            }
             confirmPasswordInput.setSelection(passwordInput.text.length)
            isPasswordVisible = !isPasswordVisible
        }

    }

    private fun signUpUser(username: String,email: String,password: String, phoneNumber: String) {
        val request = SignupRequest(username,email, password, phoneNumber)

        val service = RetrofitClient.retrofitService
        service.signUpUser(request).enqueue(object : Callback<SignupResponse> {

            override fun onResponse(
                call: Call<SignupResponse>,
                response: retrofit2.Response<SignupResponse>
            ) {
                Log.d("SIGNUP_RESPONSE", "Code: ${response.code()}")
                Log.d("SIGNUP_RESPONSE", "Body: ${response.body()}")

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