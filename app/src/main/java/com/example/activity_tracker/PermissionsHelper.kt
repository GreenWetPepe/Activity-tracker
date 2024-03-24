package com.example.activity_tracker

import android.app.Activity
import android.util.Log
import androidx.core.app.ActivityCompat

class PermissionsHelper(private val activity: Activity?) {
    private val permissionsLocation = arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION)

    fun allowLocation() {
        if (activity != null) {
            ActivityCompat.requestPermissions(activity, permissionsLocation, 0)
            Log.i("PermissionsHelper", "ACCESS_COARSE_LOCATION get access permission")
        }
    }
}