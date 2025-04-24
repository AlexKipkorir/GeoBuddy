package com.example.geobuddy

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
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
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.geobuddy.models.Tracker
import androidx.core.net.toUri
import com.example.geobuddy.retrofit.RetrofitClient
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


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    //Dashboard Functionality
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        addTrackerButton = findViewById(R.id.addTrackerButton)
        profileButton = findViewById(R.id.profileButton)
        trackerSpinner = findViewById(R.id.trackerSpinner)


        // init tracker registration button
        addTrackerButton.setOnClickListener {
            val intent = Intent(this, TrackerRegistrationActivity::class.java)
            startActivity(intent)
        }

        // init profile button
        profileButton.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // init FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // init Map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // init google maps route to tracker
        val routeButton = findViewById<ImageButton>(R.id.routeButton)
        routeButton.setOnClickListener {
            // Get the currently selected tracker from the spinner
            val pos = trackerSpinner.selectedItemPosition
            if (pos != AdapterView.INVALID_POSITION && trackerList.isNotEmpty()) {
                val tracker = trackerList[pos]
                // launch Google Maps to the tracker's location
                val uri = "google.navigation:q=${tracker.latitude},${tracker.longitude}".toUri()
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.setPackage("com.google.android.apps.maps")
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            }
        }

        // init google maps street view to see tracker surroundings
        val streetViewButton = findViewById<ImageButton>(R.id.streetViewButton)
        streetViewButton.setOnClickListener {
            val pos = trackerSpinner.selectedItemPosition
            if (pos != AdapterView.INVALID_POSITION && trackerList.isNotEmpty()) {
                val tracker = trackerList[pos]
                // launch Street View to the tracker's location
                val uri = "google.streetview:cbll=${tracker.latitude},${tracker.longitude}".toUri()
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.setPackage("com.google.android.apps.maps")
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            }
        }
    }

    //Map functionality
    override fun onMapReady(map: GoogleMap) {

        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = false

        loadTrackers()

        map.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            @SuppressLint("SetTextI18n", "InflateParams")
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
                .setMessage("Info:\n$details\n")
                .setPositiveButton("OK", null)
                .show()
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

    private fun loadTrackers() {
        val sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt_token", "") ?: ""

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
                            .title(tracker.trackerName)
                            .snippet("status")
                            .snippet(buildString {
                                append("Status: ${tracker.status}\n")
                                append("Battery: ${tracker.batteryCapacity}\n")
                                tracker.age?.let { append("Age: $it\n") }
                                append("Description: ${tracker.description}\n")
                                append("Date Created: ${tracker.timestamp}\n")
                            })
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        googleMap.addMarker(markerOptions)

                        spinnerItems.add("${tracker.trackerName} - ${tracker.status}")
                    }

                    val adapter = ArrayAdapter(this@DashboardActivity, android.R.layout.simple_spinner_dropdown_item, spinnerItems)
                    trackerSpinner.adapter = adapter

                    trackerSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            val selectedTracker = trackerList[position]
                            val targetPos = LatLng(selectedTracker.latitude, selectedTracker.longitude)
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(targetPos, 16f))
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
}