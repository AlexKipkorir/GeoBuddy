package com.example.geobuddy

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class EditProfileActivity : AppCompatActivity() {

    private lateinit var profileImageView: ImageView
    private lateinit var fullNameEditText: EditText
    private lateinit var phoneNUmberEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button


    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        profileImageView = findViewById(R.id.profileImageView)
        fullNameEditText = findViewById(R.id.fullNameEditText)
        phoneNUmberEditText = findViewById(R.id.phoneNUmberEditText)
        emailEditText = findViewById(R.id.emailEditText)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)

        //Load existing profile data
        fullNameEditText.setText("John Doe")
        phoneNUmberEditText.setText("123-456-7890")
        emailEditText.setText("james.monroe@examplepetstore.com")

        profileImageView.setOnClickListener {
            openImageChooser()
        }

        saveButton.setOnClickListener {
            saveProfile()
        }
        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data
            profileImageView.setImageURI(selectedImageUri)
        }
    }

    private fun saveProfile() {
        val fullName = fullNameEditText.text.toString()
        val phoneNumber = phoneNUmberEditText.text.toString()
        val email = emailEditText.text.toString()

        //TODO: Save profile data to Firebase or other storage

        Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show()
        finish()
    }

}
