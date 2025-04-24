package com.example.geobuddy

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.AlertDialog
import com.example.geobuddy.retrofit.RetrofitClient
import com.example.geobuddy.retrofit.ApiResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.appcompat.app.AlertDialog
import com.example.geobuddy.retrofit.MessageResponse


import com.example.geobuddy.retrofit.UserProfile


//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {
//
//    private lateinit var auth: FirebaseAuth
//    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        //Initialize Firebase Authentication and Firestore
//        auth = FirebaseAuth.getInstance()
//        db = FirebaseFirestore.getInstance()

        //UI Elements
        val profileImage = findViewById<ImageView>(R.id.profileImage)
        val userName = findViewById<TextView>(R.id.fullName)
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
        loadUserDetails(userName, phoneNumber, email)

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
           val dialog = MapSettingsActivity()
            dialog.show(supportFragmentManager, "MapSettingsDialog")
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
//            auth.signOut()
//            startActivity(Intent(this, LoginActivity::class.java))
//            finish()
            // Perform logout action (if any)
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        //More function button functionality
        moreFunctionButton.setOnClickListener {
            startActivity(Intent(this, MoreFunctionsActivity::class.java))
                    }

     }

    //Firebase
    //Load user details from Firestore
//    private fun loadUserDetails(fullName: TextView, phoneNumber: TextView, email: TextView) {
//        val user = auth.currentUser
//        user?.let {
//            val userId = it.uid
//            db.collection("users").document(userId).get()
//                .addOnSuccessListener { document ->
//                    if (document.exists()) {
//                        fullName.text = document.getString("fullName") ?: "Unknown User"
//                        phoneNumber.text = document.getString("phoneNumber") ?: "No phone number"
//                        email.text = it.email ?: "Unknown Email"
//                    }
//                }
//                .addOnFailureListener {
//                    fullName.text = "Error loading user"
//                    phoneNumber.text = ""
//                    email.text = ""
//                }
//        }
//    }
//
//    //Delete user account function
//    private fun deleteUserAccount() {
//        val user = auth.currentUser
//        user?.delete()?.addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                db.collection("users").document(user.uid).delete()
//                startActivity(Intent(this, LoginActivity::class.java))
//                finish()
//            }
//        }
//    }

    //Retrofit
    private fun loadUserDetails(userName: TextView, phoneNumber: TextView, email: TextView) {
        val sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt_token", "") ?: ""

        Log.d("TOKEN_CHECK", "Retrieved token: '$token'")


        if (token.isEmpty()) {
            Toast.makeText(this, "Token not found. Please log in again.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val authHeader = "Bearer $token"
        val retrofitService = RetrofitClient.retrofitService

        retrofitService.getUserDetails(authHeader)
            .enqueue(object : Callback<UserProfile> {
            override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                if (response.isSuccessful) {
                   val userProfile = response.body()
                   userProfile?.let {
                       userName.text = it.username
                       phoneNumber.text = it.phoneNumber
                       email.text = it.email
                   }
               } else {
                   val errorBody = response.errorBody()?.string()
                    Log.e("UserDetails", "Raw error body: $errorBody")
                   Toast.makeText(this@ProfileActivity, "Error loading user details", Toast.LENGTH_SHORT).show()
                   Log.e("UserDetails", "Error: $errorBody")
               }
            }

            override fun onFailure(call: Call<UserProfile>, t: Throwable) {
               Toast.makeText(this@ProfileActivity, "Error loading user details", Toast.LENGTH_SHORT).show()
                Log.e("UserDetails", "Network error", t)
            }
        })
    }

    private fun deleteUserAccount() {
        val prefs = getSharedPreferences("login_prefs", MODE_PRIVATE)
        val token = prefs.getString("jwt_token", "") ?: ""

        if (token.isEmpty()) {
            Toast.makeText(this, "Token not found. Please log in again.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Confirm Deletion")
            .setMessage("Are you sure you want to delete your account?")
            .setPositiveButton("Delete") { _, _ ->
                val retrofitService = RetrofitClient.retrofitService
                val authHeader = "Bearer $token"
                
                retrofitService.deleteAccount(authHeader).enqueue(object : Callback<MessageResponse>  {
                    override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                        val apiResponse = response.body()
                        if (response.isSuccessful && response.body() != null) {
                            Toast.makeText(this@ProfileActivity, "Account deleted successfully", Toast.LENGTH_SHORT).show()

                            //Clear stored login info
                            prefs.edit().clear().apply()

                            //Go to login screen
                            startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
                            finish()
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Log.e("DeleteAccount", "Raw error body: $errorBody")
                            Toast.makeText(this@ProfileActivity, "Error deleting account", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                        Toast.makeText(this@ProfileActivity, "Error deleting account", Toast.LENGTH_SHORT).show()
                        Log.e("DeleteAccount", "Network error", t)
                    }
                    
                })
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}