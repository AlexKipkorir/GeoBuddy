package com.example.geobuddy

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageView

class MainActivity : AppCompatActivity() {
    override fun onCreate (savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val logo: ImageView = findViewById(R.id.logoImgLogo)
//        val loginButton: Button = findViewById(R.id.btnLogin)
//        val signupButton: Button = findViewById(R.id.btnSignup)

        Handler(Looper.getMainLooper()).postDelayed({
           val intent = Intent(this, LoginActivity::class.java)
           startActivity(intent)
            finish()
        },2000)

//        loginButton.setOnClickListener{
//            startActivity(Intent(this, LoginActivity::class.java))
//        }
//
//        signupButton.setOnClickListener{
//            startActivity(Intent(this, SignupActivity::class.java))
//        }


    }

}
/*
1. Imports:
Intent: Used to navigate between activities.
Bundle: Required for onCreate method.
AppCompatActivity: The base class for activities.
Button: UI component for user interactions.
2. Class Definition:
Extends AppCompatActivity since it's an activity.
3. onCreate Method:
Sets the layout using setContentView(R.layout.activity_main).
Finds Login and Signup buttons using findViewById.
Adds click listeners to navigate to LoginActivity and SignupActivity.
*/