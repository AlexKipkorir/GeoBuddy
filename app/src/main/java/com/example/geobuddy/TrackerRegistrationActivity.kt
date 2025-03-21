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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
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
        registerTrackerButton.setOnClickListener{
            registerTracker()
        }
        registerTrackerButton.setOnClickListener {
            val imei = imeiInput.text.toString().trim()

            if (imei.isEmpty()) {
                statusIndicator.text = "Error: IMEI number is required"
                statusIndicator.setTextColor(Color.RED)
                sendNotification(this,"Registration Failed","IMEI number is required for registration")
            } else {
                checkTrackerStatus(imei,statusIndicator)
            }
        }

    }

    // Notification Channel
    // Function to create the notification channel
    fun createNotificationChannel(context: Context) {
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
    fun sendNotification(context: Context,title: String, message: String) {
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

    // Function to check the status of the tracker
    private fun checkTrackerStatus(imei: String, statusIndicator: TextView) {


        // Simulate status check (In reality, you'd query the database)
        if (imei == "123456789012345") {
            statusIndicator.text = "Status: Active"
            statusIndicator.setTextColor(Color.GREEN)
            sendNotification(this,"Registration Successful","Tracker registered successfully")
        } else {
            statusIndicator.text = "Status: Inactive"
            statusIndicator.setTextColor(Color.RED)
            sendNotification(this,"Registration Failed","Invalid IMEI number")
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickImageRequest && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            imageView.visibility = View.VISIBLE
            imageView.setImageURI(imageUri)
            removeImageButton.visibility = View.VISIBLE
        }
    }

    // Function to register the tracker
    private fun registerTracker() {
        val userId = auth.currentUser?.uid ?: return
        val trackerName = trackerNameInput.text.toString().trim()
        val imei = imeiInput.text.toString().trim()
        val category = categorySpinner.selectedItem.toString().trim()

        if (trackerName.isEmpty() || imei.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }
        val maxTrackersAllowed = 5
        db.collection("users").document(userId).collection("trackers").get()
            .addOnSuccessListener { documents ->
                if (documents.size() >= maxTrackersAllowed) {
                    Toast.makeText(this, "Maximum number of trackers reached", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
                imageUri?.let { uri ->
                    val imageRef = storage.child("tracker_images/${UUID.randomUUID()}.jpg")
                    imageRef.putFile(uri)
                        .addOnSuccessListener { _ ->
                            imageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                                saveTrackerToFirestore(userId, trackerName, imei, category, imageUrl.toString())
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                        }

                } ?: saveTrackerToFirestore(userId, trackerName, imei, category, null)
            }
    }

    // Function to save the tracker to Firestore
    private fun saveTrackerToFirestore(userId: String, trackerName: String, imei: String, category: String, imageUrl: String?) {
        Log.d("TrackerRegistration", "Saving tracker: userId=$userId, trackerName=$trackerName, imei=$imei, category=$category, imageUrl=$imageUrl")

        val trackerData = hashMapOf(
            "name" to trackerName,
            "imei" to imei,
            "category" to category,
            "imageUrl" to (imageUrl ?: ""),
            "status" to "Active",
            "registeredAt" to FieldValue.serverTimestamp()
        )
        // Ensure the user document exists
        val userRef = db.collection("users").document(userId)
        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // User document exists, add the tracker
                userRef.collection("trackers")
                    .add(trackerData)
                    .addOnSuccessListener { documentReference ->
                        Log.d("TrackerRegistration", "Tracker added with ID: ${documentReference.id}")
                        Toast.makeText(this, "Tracker registered successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, DashboardActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Log.e("TrackerRegistration", "Error adding tracker", e)
                        Toast.makeText(this, "Failed to register tracker", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // User document does not exist, create it first
                userRef.set(hashMapOf("createdAt" to FieldValue.serverTimestamp()))
                    .addOnSuccessListener {
                        // Now add the tracker
                        userRef.collection("trackers")
                            .add(trackerData)
                            .addOnSuccessListener { documentReference ->
                             Log.d("TrackerRegistration", "Tracker added with ID: ${documentReference.id}")
                             Toast.makeText(this, "Tracker registered successfully", Toast.LENGTH_SHORT).show()
                             startActivity(Intent(this, DashboardActivity::class.java))
                             finish()
                            }
                            .addOnFailureListener { e ->
                                Log.e("TrackerRegistration", "Error adding tracker", e)
                                Toast.makeText(this, "Failed to register tracker", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.e("TrackerRegistration", "Error creating user document", e)
                        Toast.makeText(this, "Failed to register tracker", Toast.LENGTH_SHORT).show()

                    }
            }
        }.addOnFailureListener { e ->
            Log.e("TrackerRegistration", "Error checking user document", e)
            Toast.makeText(this, "Failed to register tracker", Toast.LENGTH_SHORT).show()
        }
    }

}