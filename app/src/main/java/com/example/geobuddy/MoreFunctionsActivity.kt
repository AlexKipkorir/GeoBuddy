package com.example.geobuddy

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.datatransport.BuildConfig
import androidx.core.content.edit

class MoreFunctionsActivity : AppCompatActivity() {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var darkModeSwitch: Switch
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_functions)

        val switchPush = findViewById<Switch>(R.id.switchPushNotifications)
        val switchEmail = findViewById<Switch>(R.id.switchEmailAlerts)

        findViewById<Button>(R.id.ViewTrackersButton).setOnClickListener {
            startActivity(Intent(this, TrackersViewActivity::class.java))
        }

        findViewById<Button>(R.id.btnTerms).setOnClickListener {
            startActivity(Intent(this, TermsofServiceActivity::class.java))
        }
        findViewById<TextView>(R.id.txtAppVersion).text = "App Version: ${BuildConfig.VERSION_NAME}"
    }
}