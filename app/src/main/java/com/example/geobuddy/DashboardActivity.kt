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
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var trackerSpinner: Spinner
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var findMeButton: FloatingActionButton
    private lateinit var addTrackerButton: FloatingActionButton
    private val markers = mutableListOf<Marker>()
    private lateinit var auth: FirebaseAuth

    // Dummy tracker data
    private val trackers = listOf(
        Tracker("Tracker 1", "Active", "80%", -1.2921, 36.8219),
        Tracker("Tracker 2", "Offline", "50%", -0.1678025162622931, 35.96421574323334),
        Tracker("Tracker 3", "Active", "90%", -1.3100, 36.8050)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        addTrackerButton = findViewById(R.id.addTrackerButton)

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
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            trackers.map { "${it.name} - ${it.status} - ${it.battery}" }
        )
        trackerSpinner.adapter = adapter

        // Handle tracker selection
        trackerSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (::googleMap.isInitialized) {
                    val selectedTracker = trackers[position]
                    val location = LatLng(selectedTracker.latitude, selectedTracker.longitude)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 14f))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        auth = FirebaseAuth.getInstance()

        val profileButton = findViewById<ImageView>(R.id.profileButton)
        profileButton.setOnClickListener {
            showProfileBottomSheet()
        }
    }

    private fun showProfileBottomSheet() {
        val bottomSheetView = LayoutInflater.from(this).inflate(R.layout.profile_bottom_sheet, null)
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetView)

        val logoutButton = bottomSheetView.findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            logoutUser()
            bottomSheetDialog.dismiss() // Close bottom sheet
        }

        bottomSheetDialog.show()
    }

    private fun logoutUser() {
        auth.signOut() // Firebase Logout

        // Redirect to Login Screen
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Close MainActivity
    }

    override fun onMapReady(map: GoogleMap) {
        Log.d("DashboardActivity", "onMapReady called")
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = false


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

        // Add markers for trackers
        for (tracker in trackers) {
            val location = LatLng(tracker.latitude, tracker.longitude)
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title(tracker.name)
                    .snippet("Status: ${tracker.status}, Battery: ${tracker.battery}")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
            marker?.let { markers.add(it) }
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

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}

