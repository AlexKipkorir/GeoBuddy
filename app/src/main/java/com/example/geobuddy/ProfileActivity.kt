package com.example.geobuddy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        //Initialize Firebase Authentication and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        //UI Elements
        val profileImage = findViewById<ImageView>(R.id.profileImage)
        val fullName = findViewById<TextView>(R.id.fullName)
        val phoneNumber = findViewById<TextView>(R.id.phoneNumber)
        val email = findViewById<TextView>(R.id.email)
        val editButton = findViewById<Button>(R.id.editButton)
        val deleteButton = findViewById<Button>(R.id.deleteButton)
        val mapSettingsButton = findViewById<Button>(R.id.mapSettingsButton)
        val modifyPasswordButton = findViewById<Button>(R.id.modifyPasswordButton)
        val faqButton = findViewById<Button>(R.id.faqButton)
        val privacyPolicyButton = findViewById<Button>(R.id.privacyPolicyButton)
        val aboutUsButton = findViewById<Button>(R.id.aboutUsButton)
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        val moreFunctionButton = findViewById<ImageButton>(R.id.moreFunctionButton)

        //Load user details from Firestore
        loadUserDetails(fullName, phoneNumber, email)

        //Edit button functionality
        editButton.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        //Delete button functionality
        deleteButton.setOnClickListener {
            deleteUserAccount()
        }

        //Navigate to other pages
        mapSettingsButton.setOnClickListener {
            startActivity(Intent(this, MapSettingsActivity::class.java))
        }

        modifyPasswordButton.setOnClickListener {
            startActivity(Intent(this, ModifyPasswordActivity::class.java))
        }

        faqButton.setOnClickListener {
            startActivity(Intent(this, FAQActivity::class.java))
        }

        privacyPolicyButton.setOnClickListener {
            startActivity(Intent(this, PrivacyPolicyActivity::class.java))
        }

        aboutUsButton.setOnClickListener {
            startActivity(Intent(this, AboutUsActivity::class.java))
        }

        //Logout button functionality
        logoutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        //More function button functionality
        moreFunctionButton.setOnClickListener {
            //Open a more options menu or settings
        }

     }

    //Load user details from Firestore
    private fun loadUserDetails(fullName: TextView, phoneNumber: TextView, email: TextView) {
        val user = auth.currentUser
        user?.let {
            val userId = it.uid
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        fullName.text = document.getString("fullName") ?: "Unknown User"
                        phoneNumber.text = document.getString("phoneNumber") ?: "No phone number"
                        email.text = it.email ?: "Unknown Email"
                    }
                }
                .addOnFailureListener {
                    fullName.text = "Error loading user"
                    phoneNumber.text = ""
                    email.text = ""
                }
        }
    }

    //Delete user account function
    private fun deleteUserAccount() {
        val user = auth.currentUser
        user?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                db.collection("users").document(user.uid).delete()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }
}