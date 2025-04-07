package com.example.geobuddy

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.geobuddy.retrofit.RetrofitClient
import com.example.geobuddy.retrofit.SignupRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize views using findViewById
        val signUpButton: Button = findViewById(R.id.signUpButton)
        val usernameInput: EditText = findViewById(R.id.usernameInput)
        val emailInput: EditText = findViewById(R.id.emailInput)
        val passwordInput: EditText = findViewById(R.id.passwordInput)

        signUpButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            // Validate inputs
            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                registerUser(username, email, password)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
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
                        // Navigate to the next screen or update UI as needed.
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

/*
private lateinit var auth: FirebaseAuth

//Initialize Views
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_signup)

    //Initialize Firebase Authentication
    auth = FirebaseAuth.getInstance()

    val firstNameInput = findViewById<EditText>(R.id.firstNameInput)
    val lastNameInput = findViewById<EditText>(R.id.lastNameInput)
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
        val firstName = firstNameInput.text.toString()
        val lastName = lastNameInput.text.toString()
        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()
        val confirmPassword = confirmPasswordInput.text.toString().trim()

        if(firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return@setOnClickListener
        }

        if (password == confirmPassword) {
            //TODO: Call Retrofit API to sign up user
        } else {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
        }
        signUpUser(email, password)
    }

    //Google Sign Up Button Click Listener
    googleSignUpButton.setOnClickListener{
        //TODO: Call Google Sign Up API
    }

    //Navigate to Login Page
    signInText.setOnClickListener{
        val intent = Intent(this, OTPVerificationActivity ::class.java)
        intent.putExtra("email", emailInput.text.toString())
        startActivity(intent)
        finish()
    }

}

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
