package com.example.geobuddy

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import android.view.View
import android.widget.RadioButton
import android.widget.Toast

class MapSettingsActivity : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
       val inflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.dialog_map_settings, null)

        val googleMapsRadioButton = view.findViewById<RadioButton>(R.id.googleMapsCheckBox)
        googleMapsRadioButton.isChecked = true
        googleMapsRadioButton.isEnabled = false

        return AlertDialog.Builder(requireContext())
            .setTitle("Map Settings")
            .setView(view)
            .setPositiveButton("OK") { dialog, _ ->
               Toast.makeText(requireContext(), "Google Maps Selected", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .create()

    }

}
