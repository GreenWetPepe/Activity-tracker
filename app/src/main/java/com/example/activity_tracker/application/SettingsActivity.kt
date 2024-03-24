package com.example.activity_tracker.application

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.activity_tracker.R
import org.koin.core.KoinComponent


class SettingsActivity : AppCompatActivity(), KoinComponent {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        findViewById<ConstraintLayout>(R.id.forgetDeviceLayout).setOnClickListener {
            DialogDeleteDevice(this).createDialog()
        }

        findViewById<ConstraintLayout>(R.id.deleteMeasurementsLayout).setOnClickListener {
            DialogDeleteMeasurements(this).createDialog()
        }
    }
}
