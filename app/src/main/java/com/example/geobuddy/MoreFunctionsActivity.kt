package com.example.geobuddy

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MoreFunctionsActivity : AppCompatActivity() {
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.more_functions_activity)

        val viewTrackersButton = findViewById<Button>(R.id.viewTrackersButton)
        val darkModeSwitch = findViewById<Switch>(R.id.darkModeSwitch)
        val notificationsSwitch = findViewById<Switch>(R.id.notificationsSwitch)

        viewTrackersButton.setOnClickListener {
            val intent = Intent(this, TrackersViewActivity::class.java)
            startActivity(intent)
        }

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(this, "Dark mode: $isChecked", Toast.LENGTH_SHORT).show()
        }

        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(this, "Notifications: $isChecked", Toast.LENGTH_SHORT).show()
        }
    }
}
