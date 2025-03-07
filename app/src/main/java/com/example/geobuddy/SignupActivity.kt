package com.example.geobuddy

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

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
}