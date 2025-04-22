package com.example.geobuddy

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.geobuddy.retrofit.ChildTrackerRequest
import com.example.geobuddy.retrofit.LuggageTrackerRequest
import com.example.geobuddy.retrofit.PetTrackerRequest
import com.example.geobuddy.retrofit.RetrofitClient
import com.example.geobuddy.retrofit.RetrofitClient.retrofitService
import com.example.geobuddy.retrofit.TrackerImeiResponse
import com.example.geobuddy.retrofit.TrackerRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

private const val CHANNEL_ID = "tracker_status_channel"
private const val NOTIFICATION_ID = 1

class TrackerRegistrationActivity : AppCompatActivity() {
    private lateinit var categorySpinner: Spinner
    private lateinit var layoutFields: LinearLayout
    private lateinit var trackerNameInput: EditText
    private lateinit var imeiInput: EditText
    private lateinit var luggageNameInput: EditText
    private lateinit var petNameInput: EditText
    private lateinit var childNameInput: EditText
    private lateinit var petAgeInput: EditText
    private lateinit var childAgeInput: EditText
    private lateinit var colorInput: EditText
    private lateinit var luggageDescriptionInput: EditText
    private lateinit var petDescriptionInput: EditText
    private lateinit var childDescriptionInput: EditText
    private lateinit var breedInput: EditText
    private lateinit var uploadImageButton: Button
    private lateinit var removeImageButton: Button
    private lateinit var statusIndicator: TextView
    private lateinit var registerTrackerButton: Button
    private lateinit var imageView: ImageView
    private var imageUri: Uri? = null
    private val pickImageRequest = 1

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracker_registration)

        categorySpinner = findViewById(R.id.categorySpinner)
        layoutFields = findViewById(R.id.layoutFields)
        trackerNameInput = findViewById(R.id.trackerNameInput)
        imeiInput = findViewById(R.id.imeiInput)
        luggageNameInput = findViewById(R.id.luggageNameInput)
        petNameInput = findViewById(R.id.petNameInput)
        childNameInput = findViewById(R.id.childNameInput)
        petAgeInput = findViewById(R.id.petAgeInput)
        childAgeInput = findViewById(R.id.childAgeInput)
        colorInput = findViewById(R.id.colorInput)
        luggageDescriptionInput = findViewById(R.id.luggageDescriptionInput)
        petDescriptionInput = findViewById(R.id.petDescriptionInput)
        childDescriptionInput = findViewById(R.id.childDescriptionInput)
        breedInput = findViewById(R.id.breedInput)
        uploadImageButton = findViewById(R.id.uploadImageButton)
        removeImageButton = findViewById(R.id.removeImageButton)
        registerTrackerButton = findViewById(R.id.registerTrackerButton)
        imageView = findViewById(R.id.imageView)
        statusIndicator = findViewById(R.id.statusIndicator)

        setupCategorySelection()

        uploadImageButton.setOnClickListener {
            openFileChooser()
        }
        removeImageButton.setOnClickListener {
            removeSelectedImage()
        }

        registerTrackerButton.setOnClickListener {
            Log.d("TrackerRegistrationActivity", "Button clicked: Register Tracker")

            val imei = imeiInput.text.toString().trim()

            if (imei.isEmpty()) {
                statusIndicator.text = "Error: IMEI number is required"
                statusIndicator.setTextColor(Color.RED)
                sendNotification(this,"Registration Failed","IMEI number is required for registration")
            } else {
                registerTracker()
            }
        }

    }

    // Notification Channel
    // Function to create the notification channel
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Tracker Status"
            val descriptionText = "Notification for tracker registration status"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID,name,importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    // Function to send a notification
    private fun sendNotification(context: Context,title: String, message: String) {
        // Create a notification
        createNotificationChannel(context)
        //Check if permission is granted (Android 13+ requires it)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
                // Log Warning (optional)
                println("Notification permission not granted")
                return // Exit the function if permission is not granted
            }
        }

        //Build and Show the notification
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }
    // Handle permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                sendNotification(this, "Registration Successful", "Tracker registered successfully")
            } else {
                println("Permission denied. Notification will not be sent.")
            }
        }
    }


    // Function to set up the category selection
    private fun setupCategorySelection() {
        val categories = arrayOf("Select Category", "Luggage", "Pet", "Child")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        categorySpinner.adapter = adapter


        categorySpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = parent?.getItemAtPosition(position).toString()

                if (selectedCategory == "Luggage" || selectedCategory == "Pet" || selectedCategory == "Child") {
                    layoutFields.visibility = View.VISIBLE
                } else {
                    layoutFields.visibility = View.GONE
                }
                updateFieldsVisibility(selectedCategory)

            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    // Function to animate the visibility of views
    private fun animateVisibility(view: View, show: Boolean) {
        if (show) {
            view.visibility = View.VISIBLE
            view.alpha = 0f
            view.translationY = -50f
            view.animate()
                .alpha(1f)
                .translationYBy(50f)
                .setDuration(300)
                .setInterpolator(DecelerateInterpolator())
                .start()
        } else {
            view.animate()
                .alpha(0f)
                .translationYBy(-50f)
                .setDuration(300)
                .setInterpolator(DecelerateInterpolator())
                .withEndAction {
                    view.visibility = View.GONE
                }
                .start()
        }
    }

    // Function to update the visibility of fields based on the selected category
    private fun updateFieldsVisibility(category: String) {
        // Hide all fields first
        listOf(
            luggageNameInput,petNameInput,childNameInput,colorInput,breedInput,
            petAgeInput,childAgeInput,luggageDescriptionInput,petDescriptionInput,childDescriptionInput)
            .forEach{ animateVisibility(it, false)}
        // Show relevant fields based on category
        when (category) {
            "Luggage" -> {
                animateVisibility(luggageNameInput, true)
                animateVisibility(colorInput, true)
                animateVisibility(luggageDescriptionInput, true)
            }
            "Pet" -> {
                animateVisibility(petNameInput, true)
                animateVisibility(breedInput, true)
                animateVisibility(petAgeInput, true)
                animateVisibility(petDescriptionInput, true)
            }
            "Child" -> {
                animateVisibility(childNameInput, true)
                animateVisibility(childAgeInput, true)
                animateVisibility(childDescriptionInput, true)
            }
        }
    }

    // Function to open the image chooser
    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, pickImageRequest)
    }

    // Function to remove the selected image
    private fun removeSelectedImage() {
        imageUri = null
        imageView.setImageDrawable(null)
        imageView.visibility = View.GONE
        removeImageButton.visibility = View.GONE
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickImageRequest && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            imageView.visibility = View.VISIBLE
            imageView.setImageURI(imageUri)
            removeImageButton.visibility = View.VISIBLE
        }
    }

    // Function For Save Tracker Button
    private fun registerTracker() {
        Log.d("TrackerRegistrationActivity", "Function called: registerTracker")

        val prefs = getSharedPreferences("login_prefs", MODE_PRIVATE)
        val token = prefs.getString("jwt_token", "") ?: ""

        if (token.isEmpty()) {
            Toast.makeText(this, "Authentication token not found. Please log in again.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val trackerName = trackerNameInput.text.toString().trim()
        val imei = imeiInput.text.toString().trim()
        val category = categorySpinner.selectedItem.toString().trim()

        if (trackerName.isEmpty() || imei.isEmpty() || category == "Select Category") {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val authHeader = "Bearer $token"

        // Directly proceed to save tracker
        saveTrackerToBackend(authHeader, trackerName, imei, category)
    }



    // Save tracker to Firestore
//    private fun saveTrackerToFirestore(userId: String, trackerName: String, imei: String, category: String, imageUrl: String?) {
//        Log.d("TrackerRegistrationActivity", "Function called: saveTrackerToFirestore")
//        Log.d("TrackerRegistrationActivity", "Saving tracker to Firestore: userId=$userId, trackerName=$trackerName, imei=$imei, category=$category, imageUrl=$imageUrl")
//
//        val trackerData = hashMapOf(
//            "userId" to userId,
//            "trackerName" to trackerName,
//            "imei" to imei,
//            "trackerType" to category,
//            "imageUrl" to (imageUrl ?: ""),
//            "trackerStatus" to "Active",
//            "latitude" to 0.0,
//            "longitude" to 0.0,
//            "battery" to "100%",
//            "createdAt" to FieldValue.serverTimestamp()
//        )
//
//        Log.d("TrackerRegistrationActivity", "Attempting to write data to Firestore")
//
//        db.collection("trackers")
//            .add(trackerData)
//            .addOnSuccessListener { documentReference ->
//                Log.d("TrackerRegistrationActivity", "Tracker saved with ID: ${documentReference.id}")
//                showToast("Tracker registered successfully")
//                navigateToDashboard()
//            }
//            .addOnFailureListener { e ->
//                Log.e("FirestoreError", "Failed To write Data")
//                Log.e("TrackerRegistrationActivity", "Error saving tracker", e)
//                showToast("Failed to register tracker")
//            }
//
//    }
    //Retrofit
    private fun saveTrackerToBackend(token: String, trackerName: String, imei: String, category: String) {
        val retrofitService = RetrofitClient.retrofitService
        val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"


        Log.d("TOKEN_CHECK", "Retrieved token: '$token'")

        registerTrackerButton.isEnabled = false
        registerTrackerButton.setText(R.string.registering)

        // Check if IMEI exists
        retrofitService.getTrackerByImei(authHeader, imei).enqueue(object : Callback<TrackerImeiResponse> {
            override fun onResponse(call: Call<TrackerImeiResponse>, response: Response<TrackerImeiResponse>) {
                Log.d("IMEI_CHECK", "Response Code: ${response.code()}")
                Log.d("IMEI_CHECK", "Response Body: ${response.body()}")
                Log.d("IMEI_CHECK", "Error Body: ${response.errorBody()?.string()}")

                if (response.isSuccessful && response.body() != null) {
                    val imeiData = response.body()
                    Log.d("IMEI_CHECK", "Valid IMEI: $imeiData")

                    statusIndicator.text = "Status: Active"
                    statusIndicator.setTextColor(Color.GREEN)

                    when (category.lowercase()) {
                        "pet" -> {
                            val petRequest = PetTrackerRequest(
                                trackerName = trackerName,
                                imei = imei,
                                batteryCapacity = imeiData?.batteryCapacity ?: 0.0,
                                latitude = imeiData?.latitude ?: 0.0,
                                longitude = imeiData?.longitude ?: 0.0,
                                status = imeiData?.status ?: "Active",
                                breed = breedInput.text.toString(),
                                age = petAgeInput.text.toString().toIntOrNull() ?: 0,
                                description = petDescriptionInput.text.toString()
                            )

                            Log.d("REGISTER_PET", "Sending pet tracker request: $petRequest")

                            retrofitService.registerPetTracker(authHeader, petRequest).enqueue(object : Callback<Void> {
                                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                    if (response.isSuccessful) {
                                        sendNotification(this@TrackerRegistrationActivity, "Registration Successful", "Pet tracker registered successfully")
//                                        showToast("Pet tracker registered successfully")
                                        navigateToDashboard()
                                    } else {
                                        val error = response.errorBody()?.string()
                                        Log.e("REGISTER_PET", "Error: $error")
                                        showToast("Failed to register pet tracker")
                                        resetButton()
                                    }
                                }

                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                    Log.e("REGISTER_PET", "Network error: ${t.message}", t)
                                    showToast("Failed to register pet tracker: ${t.message}")
                                    resetButton()
                                }
                            })
                        }

                        "child" -> {
                            val childRequest = ChildTrackerRequest(
                                trackerName = trackerName,
                                imei = imei,
                                batteryCapacity = imeiData?.batteryCapacity ?: 0.0,
                                latitude = imeiData?.latitude ?: 0.0,
                                longitude = imeiData?.longitude ?: 0.0,
                                status = imeiData?.status ?: "Active",
                                age = childAgeInput.text.toString().toIntOrNull() ?: 0,
                                description = childDescriptionInput.text.toString(),
                                name = childNameInput.text.toString()
                            )

                            retrofitService.registerChildTracker(authHeader, childRequest).enqueue(object : Callback<Void> {
                                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                    if (response.isSuccessful) {
                                        sendNotification(this@TrackerRegistrationActivity, "Registration Successful", "Child tracker registered successfully")
//                                        showToast("Child tracker registered")
                                        navigateToDashboard()
                                    } else {
                                        showToast("Failed to register child tracker")
                                        resetButton()
                                    }
                                }

                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                    showToast("Error saving child tracker: ${t.message}")
                                    resetButton()
                                }
                            })
                        }

                        "luggage" -> {
                            val luggageRequest = LuggageTrackerRequest(
                                trackerName = trackerName,
                                imei = imei,
                                batteryCapacity = imeiData?.batteryCapacity ?: 0.0,
                                latitude = imeiData?.latitude ?: 0.0,
                                longitude = imeiData?.longitude ?: 0.0,
                                status = imeiData?.status ?: "Active",
                                color = colorInput.text.toString(),
                                description = luggageDescriptionInput.text.toString(),
                                name = luggageNameInput.text.toString()
                            )

                            retrofitService.registerLuggageTracker(authHeader, luggageRequest).enqueue(object : Callback<Void> {
                                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                    if (response.isSuccessful) {
                                        sendNotification(this@TrackerRegistrationActivity, "Registration Successful", "Luggage tracker registered successfully")
//                                        showToast("Luggage tracker registered")
                                        navigateToDashboard()
                                    } else {
                                        showToast("Failed to register luggage tracker")
                                        resetButton()
                                    }
                                }

                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                    showToast("Error saving luggage tracker: ${t.message}")
                                    resetButton()
                                }
                            })
                        }
                    }

                } else {
                    statusIndicator.text = "Status: Inactive"
                    statusIndicator.setTextColor(Color.RED)
                    registerTrackerButton.isEnabled = true
                    registerTrackerButton.setText(R.string.register_tracker)
                    sendNotification(this@TrackerRegistrationActivity, "Registration Failed", "Invalid IMEI number")
                }
            }

            override fun onFailure(call: Call<TrackerImeiResponse>, t: Throwable) {
                showToast("Failed to verify IMEI: ${t.message}")
                Log.e("IMEI_FETCH_ERROR", "Message: ${t.message}")
                resetButton()
            }
        })
    }

    // Helper to reset button state
    private fun resetButton() {
        registerTrackerButton.isEnabled = true
        registerTrackerButton.setText(R.string.register_tracker)
    }



    // Show Toast
    private fun showToast(message: String) {
        Log.d("TrackerRegistrationActivity", "Function called: showToast")
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    // Navigate To Dashboard
    private fun navigateToDashboard() {
        Log.d("TrackerRegistrationActivity", "Function called: navigateToDashboard")
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }

}