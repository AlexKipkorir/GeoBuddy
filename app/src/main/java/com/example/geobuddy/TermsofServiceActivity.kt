package com.example.geobuddy

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class TermsofServiceActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_of_service)

        val termsText = findViewById<TextView>(R.id.txtTerms)
        termsText.text = """
            Terms of Service

            Welcome to GeoBuddy! By using this app, you agree to the following terms:

            1. Usage: This app is intended for personal tracking use only.
            2. Privacy: We respect your data. It is only used to provide the tracking features.
            3. Safety: Do not rely solely on GeoBuddy for emergency tracking.
            4. Updates: Features and terms may change with app updates.
            5. Liability: We are not responsible for losses due to app misuse or failure.

            By continuing to use the app, you accept these terms.
        """.trimIndent()
    }
}
