package com.example.geobuddy

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.datatransport.BuildConfig
import androidx.core.content.edit

class MoreFunctionsActivity : AppCompatActivity() {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var darkModeSwitch: Switch
    private lateinit var prefs: SharedPreferences

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_functions)

        val sharedPrefs = getSharedPreferences("settings_prefs", MODE_PRIVATE)

        val switchNotifications = findViewById<Switch>(R.id.switchPushNotifications)
        val switchTrackerAlerts = findViewById<Switch>(R.id.switchEmailAlerts)

// Load saved preferences
        switchNotifications.isChecked = sharedPrefs.getBoolean("notifications_enabled", true)
        switchTrackerAlerts.isChecked = sharedPrefs.getBoolean("tracker_alerts_enabled", true)

        findViewById<Button>(R.id.ViewTrackersButton).setOnClickListener {
            startActivity(Intent(this, TrackersViewActivity::class.java))
        }

        findViewById<Button>(R.id.btnTerms).setOnClickListener {
            startActivity(Intent(this, TermsofServiceActivity::class.java))
        }
        findViewById<TextView>(R.id.txtAppVersion).text = "App Version: ${BuildConfig.VERSION_NAME}"
    }
}