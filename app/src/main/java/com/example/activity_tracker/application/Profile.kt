package com.example.activity_tracker.application

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.activity_tracker.R

class Profile : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        view.findViewById<ConstraintLayout>(R.id.dataProfileLayout).setOnClickListener {
            val dataIntent = Intent(context, DataActivity::class.java)
            startActivity(dataIntent)
        }

        view.findViewById<ConstraintLayout>(R.id.activitiesProfileLayout).setOnClickListener {
            val motionsIntent = Intent(context, MotionsActivity::class.java)
            startActivity(motionsIntent)
        }

        view.findViewById<ConstraintLayout>(R.id.settingsProfileLayout).setOnClickListener {
            val settingIntent = Intent(context, SettingsActivity::class.java)
            startActivity(settingIntent)
        }
        return view
    }
}
