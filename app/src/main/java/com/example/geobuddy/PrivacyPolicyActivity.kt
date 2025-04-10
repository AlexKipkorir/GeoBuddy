package com.example.geobuddy

import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PrivacyPolicyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.privacy_policy)

        val policyText = findViewById<TextView>(R.id.privacyContent)
        val acceptCheckbox = findViewById<CheckBox>(R.id.acceptCheckbox)

        policyText.text = getPrivacyPolicyText()

        acceptCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Privacy policy accepted", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun getPrivacyPolicyText(): String {
        return """
            GeoBuddy Privacy Policy
            
            Last Updated: April 10, 2025
            
            1. Introduction
            GeoBuddy is committed to protecting your privacy. This Privacy Policy outlines how we collect, use, and protect your personal information when using our mobile application.

            2. Information We Collect
            • Personal Information: Name, email address, phone number, and account credentials.
            • Location Data: Real-time location tracking (only with user consent) for devices tagged to your profile.
            • Device Information: Device type, operating system, and usage patterns for analytics and performance.

            3. How We Use Your Data
            • To provide tracking services for your luggage, pets, and loved ones.
            • To send verification emails (e.g., OTP codes).
            • To communicate important account-related updates.
            • To analyze app usage and improve user experience.

            4. Sharing Your Information
            We do not sell or rent your personal data. Your data is only shared:
            • With trusted backend systems for secure storage.
            • With your explicit consent.
            • If required by law enforcement or regulatory authorities.

            5. Data Security
            • All passwords are encrypted using industry-standard hashing algorithms.
            • User information is stored securely in a protected database.
            • Data is transmitted over secure (HTTPS) connections.

            6. Your Rights
            • You can request access to your stored information.
            • You can request deletion of your account and data.
            • You can withdraw location tracking permissions anytime.

            7. Cookies and Analytics
            • We may use analytics tools to monitor usage trends (no personally identifiable data is collected).
            • We do not use cookies in the mobile application.

            8. Children’s Privacy
            • GeoBuddy is designed for tracking children, but account creation must be done by a parent or guardian.

            9. Changes to this Policy
            • We may update this policy periodically. Users will be notified of major changes through the app.

            10. Contact Us
            • If you have any questions or concerns, reach out to us at support at @geobuddy4@gmail.com
            
            Thank you for trusting GeoBuddy.
        """.trimIndent()
    }

}
