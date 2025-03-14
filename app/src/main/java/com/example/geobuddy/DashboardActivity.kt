package com.example.geobuddy

import android.Manifest
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
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DashboardActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var trackerSpinner: Spinner
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var findMeButton: FloatingActionButton
    private val markers = mutableListOf<Marker>()

    // Dummy tracker data (around Nairobi)
    private val trackers = listOf(
        Tracker("Tracker 1", "Active", "80%", -1.2921, 36.8219),
        Tracker("Tracker 2", "Offline", "50%", -1.3000, 36.8233),
        Tracker("Tracker 3", "Active", "90%", -1.3100, 36.8050)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

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

        // Profile button click (placeholder)
        findViewById<ImageView>(R.id.profileButton).setOnClickListener {
            // TODO: Implement profile view functionality
        }

        // Find Me Button
        findMeButton = findViewById(R.id.findMeButton)
        findMeButton.setOnClickListener {
            findUserLocation()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true

        // Check for location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
            findUserLocation()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
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
