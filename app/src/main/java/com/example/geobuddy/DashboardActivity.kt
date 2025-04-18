package com.example.geobuddy

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.geobuddy.models.Tracker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.core.net.toUri
import com.example.geobuddy.retrofit.RetrofitClient
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.Marker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DashboardActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var trackerSpinner: Spinner
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var findMeButton: FloatingActionButton
    private lateinit var addTrackerButton: FloatingActionButton
    private lateinit var profileButton: ImageView
    private val trackerList = mutableListOf<Tracker>()
//    private lateinit var auth: FirebaseAuth
//    private lateinit var db: FirebaseFirestore

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    //Dashboard Functionality
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)


        addTrackerButton = findViewById(R.id.addTrackerButton)
        profileButton = findViewById(R.id.profileButton)


//        auth = FirebaseAuth.getInstance()
//        db = FirebaseFirestore.getInstance()

        // Navigate To Tracker Registration Page
        addTrackerButton.setOnClickListener {
            val intent = Intent(this, TrackerRegistrationActivity::class.java)
            startActivity(intent)
        }

        //Profile Button init
        profileButton.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize Map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val routeButton = findViewById<ImageButton>(R.id.routeButton)
        routeButton.setOnClickListener {
            // Get the currently selected tracker from the spinner
            val pos = trackerSpinner.selectedItemPosition
            if (pos != AdapterView.INVALID_POSITION && trackerList.isNotEmpty()) {
                val tracker = trackerList[pos]
                // Create an intent to launch Google Maps to the tracker's location
                val uri = "google.navigation:q=${tracker.latitude},${tracker.longitude}".toUri()
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.setPackage("com.google.android.apps.maps")
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            }
        }

        val streetViewButton = findViewById<ImageButton>(R.id.streetViewButton)
        streetViewButton.setOnClickListener {
            val pos = trackerSpinner.selectedItemPosition
            if (pos != AdapterView.INVALID_POSITION && trackerList.isNotEmpty()) {
                val tracker = trackerList[pos]
                // Create an intent to launch Street View to the tracker's location
                val uri = "google.streetview:cbll=${tracker.latitude},${tracker.longitude}".toUri()
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.setPackage("com.google.android.apps.maps")
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            }
        }
        // Set up tracker dropdown
        trackerSpinner = findViewById(R.id.trackerSpinner)
    }

    //Map functionality
    override fun onMapReady(map: GoogleMap) {
        Log.d("DashboardActivity", "onMapReady called")
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = false

        loadTrackers()

        map.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoContents(marker: Marker): View? {
                val view = LayoutInflater.from(this@DashboardActivity).inflate(R.layout.info_window, null)
                val title = view.findViewById<TextView>(R.id.title)
                val snippet = view.findViewById<TextView>(R.id.snippet)
                val viewDetails = view.findViewById<TextView>(R.id.detailsButton)

                title.text = marker.title
                snippet.text = marker.snippet
                viewDetails.text = "View Details"
                return view
            }

            override fun getInfoWindow(marker: Marker): View? {
                return null
            }
        })

        map.setOnInfoWindowClickListener { marker ->
            val trackerName = marker.title
            val details = marker.snippet
            AlertDialog.Builder(this@DashboardActivity)
                .setTitle("Details for $trackerName")
                .setMessage("Full info:\n$details\n")
                .setPositiveButton("OK", null)
                .show()
        }



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
//
private fun loadTrackers() {
    val prefs = getSharedPreferences("login_prefs", MODE_PRIVATE)
    val token = prefs.getString("jwt_token", "") ?: ""

    if (token.isEmpty()) {
        Toast.makeText(this, "Authentication token not found. Please log in again.", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
        return
    }

    val authHeader = "Bearer $token"
    val call = RetrofitClient.retrofitService.getTrackers(authHeader)

    call.enqueue(object : Callback<List<Tracker>> {
        override fun onResponse(call: Call<List<Tracker>>, response: Response<List<Tracker>>) {
            if (response.isSuccessful && response.body() != null) {
                val trackers = response.body()!!
                trackerList.clear()
                googleMap.clear()

                val spinnerItems = mutableListOf<String>()

                for (tracker in trackers) {
                    trackerList.add(tracker)

                    val position = LatLng(tracker.latitude, tracker.longitude)
                    val markerOptions = MarkerOptions()
                        .position(position)
                        .title(tracker.name)
                        .snippet("Status: ${tracker.status}, Battery: ${tracker.battery}")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    googleMap.addMarker(markerOptions)

                    spinnerItems.add("${tracker.name} - ${tracker.status}")
                }

                val adapter = ArrayAdapter(this@DashboardActivity, android.R.layout.simple_spinner_dropdown_item, spinnerItems)
                trackerSpinner.adapter = adapter

                trackerSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val selectedTracker = trackerList[position]
                        val targetPos = LatLng(selectedTracker.latitude, selectedTracker.longitude)
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(targetPos, 14f))
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }

            } else {
                Toast.makeText(this@DashboardActivity, "Failed to load trackers: ${response.code()}", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<List<Tracker>>, t: Throwable) {
            Toast.makeText(this@DashboardActivity, "Error loading trackers: ${t.message}", Toast.LENGTH_SHORT).show()
        }
    })
}

//    private fun loadTrackers() {
//        val userId = auth.currentUser?.uid ?: return
//
//        // Query the "trackers" collection for documents with matching userId.
//        db.collection("trackers")
//            .whereEqualTo("userId", userId)
//            .get()
//            .addOnSuccessListener { querySnapshot ->
//                // Clear any previous markers and tracker list
//                googleMap.clear()
//                trackerList.clear()
//
//                // Prepare a list for spinner display strings
//                val spinnerItems = mutableListOf<String>()
//
//                for (document in querySnapshot) {
//                    // Retrieve fields from the document
//                    val name = document.getString("trackerName") ?: "Unknown Tracker"
//                    val status = document.getString("trackerStatus") ?: "Unknown"
//                    val battery = document.getString("battery") ?: "N/A"
//                    val lat = document.getDouble("latitude") ?: 0.0
//                    val lng = document.getDouble("longitude") ?: 0.0
//
//                    // Create a Tracker instance
//                    val tracker = Tracker(name, status, battery, lat, lng)
//                    trackerList.add(tracker)
//
//                    // Plot a marker on the map for each tracker
//                    val position = LatLng(lat, lng)
//                    val markerOptions = MarkerOptions()
//                        .position(position)
//                        .title(name)
//                        .snippet("Status: $status, Battery: $battery")
//                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
//                    googleMap.addMarker(markerOptions)
//
//                    // Prepare spinner item text
//                    spinnerItems.add("$name - $status")
//                }
//
//                // Set up the spinner adapter with tracker names and statuses
//                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, spinnerItems)
//                trackerSpinner.adapter = adapter
//
//                // Set spinner listener to move the map camera to the selected tracker.
//                trackerSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                        // Retrieve the selected tracker and animate the camera to its location.
//                        val selectedTracker = trackerList[position]
//                        val targetPos = LatLng(selectedTracker.latitude, selectedTracker.longitude)
//                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(targetPos, 14f))
//                    }
//
//                    override fun onNothingSelected(parent: AdapterView<*>?) {
//                        // No action needed
//                    }
//                }
//
//                // Optionally, if there are trackers, move the camera to the first one.
//                if (trackerList.isNotEmpty()) {
//                    val firstTracker = trackerList[0]
//                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(firstTracker.latitude, firstTracker.longitude), 12f))
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.e("MainActivity", "Error fetching trackers: ", exception)
//            }
//    }
}