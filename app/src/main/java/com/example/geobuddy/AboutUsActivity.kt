package com.example.geobuddy

import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AboutUsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_us)

        val aboutText = findViewById<TextView>(R.id.aboutContent)
        val backCheckBox = findViewById<CheckBox>(R.id.backCheckbox)

        aboutText.text = getAboutUsText()

        backCheckBox.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Redirecting to Profile...", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, ProfileActivity::class.java))
                finish()
            }
        }

    }

    private fun getAboutUsText(): String {
        return """
          Welcome to GeoBuddy!

            GeoBuddy is a smart and reliable tracking solution designed to help users keep track of their most valuable items – from luggage to pets to children. Whether you're traveling, working, or just on the move, GeoBuddy ensures peace of mind by keeping everything important within digital reach.

           Our Mission:
            To provide a secure, efficient, and user-friendly tracking experience by combining hardware and software into one seamless ecosystem.

           What the App Offers:
            - Real-time location tracking using GF-21 GPS device
            - Profile management
            - Secure user signup/login with OTP verification
            - Financial management integration (coming soon!)
            - Privacy-first approach

           Our Team:
            - Alex – Front-end developer (GeoBuddy Android app)
            - Dylan – Front-end developer (GeoBuddy Android app)
            - Blair – Backend developer (Authentication & APIs)
            - Dennis – Web admin portal developer

           Technologies:
            Kotlin, Spring Boot,REACT, Retrofit, XML layouts, MySQL, GF-21 tracker, and more.

            We believe in building tech that helps real people solve real problems.

            Thank you for choosing GeoBuddy.
        """.trimIndent()
    }



}
