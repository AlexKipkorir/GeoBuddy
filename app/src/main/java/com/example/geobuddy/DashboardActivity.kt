package com.example.geobuddy

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DashboardActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var trackerSpinner: Spinner
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var findMeButton: FloatingActionButton
    private lateinit var addTrackerButton: FloatingActionButton
    private val trackerList = mutableListOf<Tracker>()
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    //Dashboard Functionality
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        addTrackerButton = findViewById(R.id.addTrackerButton)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Navigate To Tracker Registration Page
        addTrackerButton.setOnClickListener {
            val intent = Intent(this, TrackerRegistrationActivity::class.java)
            startActivity(intent)
        }
        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize Map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)


        // Set up tracker dropdown
        trackerSpinner = findViewById(R.id.trackerSpinner)

        //Profile button init
        val profileButton = findViewById<ImageView>(R.id.profileButton)
        profileButton.setOnClickListener {
            showProfileBottomSheet()
        }
    }

    //Profile Button Functionality
    private fun showProfileBottomSheet() {
        val bottomSheetView = LayoutInflater.from(this).inflate(R.layout.profile_bottom_sheet, null)
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetView)

        val logoutButton = bottomSheetView.findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            logoutUser()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    //Logout Functionality
    private fun logoutUser() {
        auth.signOut() // Firebase Logout

        // Redirect to Login Screen
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Close MainActivity
    }

    //Map functionality
    override fun onMapReady(map: GoogleMap) {
        Log.d("DashboardActivity", "onMapReady called")
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = false

        loadTrackersFromFirestore()

        // Check for location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
            findUserLocation()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
        // Find Me Button
        findMeButton = findViewById(R.id.findMeButton)
        findMeButton.setOnClickListener {
            if (::googleMap.isInitialized) {
                findUserLocation()
            } else {
                Log.e("DashboardActivity", "Google Map is not initialized.")
            }
        }
    }

    // Move the map to the user's current location
    private fun findUserLocation() {
        if (!::googleMap.isInitialized) {
            Log.e("DashboardActivity", "Google Map is not initialized.")
            return
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val userLatLng = LatLng(it.latitude, it.longitude)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
                }
            }
        }
    }

    // Load Trackers from db
    private fun loadTrackersFromFirestore() {
        val userId = auth.currentUser?.uid ?: return

        // Query db trackers collection
        db.collection("trackers")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Clear any previous markers on the map and list
                googleMap.clear()
                trackerList.clear()

                // List for spinner items
                val spinnerItems = mutableListOf<String>()
                for (document in querySnapshot) {
                    // Retrieve fields from document
                    val name = document.getString("trackerName") ?: "Unknown Tracker"
                    val status = document.getString("trackerStatus") ?: "Unknown"
                    val battery = document.getString("battery") ?: "N/A"
                    val lat = document.getDouble("latitude") ?: 0.0
                    val lng = document.getDouble("longitude") ?: 0.0

                    // Create a Tracker instance
                    val tracker = Tracker(name, status, battery, lat, lng)
                    trackerList.add(tracker)

                    // Plot marker on the map
                    val position = LatLng(lat, lng)
                    val markerOptions = MarkerOptions()
                        .position(position)
                        .title(name)
                        .snippet("Status: $status, Battery: $battery")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    googleMap.addMarker(markerOptions)

                    // Prepare spinner item text
                    spinnerItems.add("$name - $status")
                }

                // Set up spinner adapter with tracker names and statuses
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, spinnerItems)
                trackerSpinner.adapter = adapter

                trackerSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        // When a tracker is selected, animate the map camera to its location.
                        val selectedTracker = trackerList[position]
                        val targetPos = LatLng(selectedTracker.latitude, selectedTracker.longitude)
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(targetPos, 14f))
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) { }
                }

                // Move camera to the first one.
                if (trackerList.isNotEmpty()) {
                    val firstTracker = trackerList[0]
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(firstTracker.latitude, firstTracker.longitude), 12f))
                }
            }
            .addOnFailureListener { exception ->
                Log.e("MainActivity", "Error fetching trackers: ", exception)
            }
    }
}