package com.example.geobuddy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()

        val emailInput: EditText = findViewById(R.id.emailInput)
        val passwordInput: EditText = findViewById(R.id.passwordInput)
        val signInButton: Button = findViewById(R.id.signInButton)
        val googleSignInButton: ImageView = findViewById(R.id.googleSignInButton)
        val signUpText: TextView = findViewById(R.id.signUpText)
        val forgotPasswordText: TextView = findViewById(R.id.forgotPasswordText)

        // Sign In Button Click Listener
        signInButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

           if (email.isEmpty() || password.isEmpty()) {
               Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
               return@setOnClickListener
           }

            signInUser(email, password)
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
    }

    //Function to sign in user
    private fun signInUser(email: String,password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful) {
                    Toast.makeText(this, "Sign in Successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Sign in Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}