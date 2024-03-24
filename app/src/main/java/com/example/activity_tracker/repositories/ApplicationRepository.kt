package com.example.activity_tracker.repositories

import android.content.Context
import android.content.SharedPreferences
import org.koin.core.KoinComponent

class ApplicationRepository(context: Context) : KoinComponent {
    private val prefs: SharedPreferences = context.getSharedPreferences("com.activity.common", 0)

    var firstLaunch: Boolean
        get() {
            return prefs.getBoolean("FirstLaunch", true)
        }
        set(value) {
            prefs.edit().putBoolean("FirstLaunch", value).apply()
        }

    fun getCurrentUser(): String? {
        return prefs.getString("User Name", "No name")
    }

    fun setCurrentUser(userName: String) {
        prefs.edit().putString("User Name", userName).apply()
    }
}