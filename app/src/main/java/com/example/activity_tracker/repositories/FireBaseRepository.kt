package com.example.activity_tracker.repositories

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import org.koin.core.KoinComponent

class FireBaseRepository(context: Context) : KoinComponent {
    private val prefs: SharedPreferences = context.getSharedPreferences("com.activity.fireBase", 0)
    private val key = "Wait"

    fun getUnPushedFiles(): MutableSet<String>? = prefs.getStringSet(key, setOf())

    fun addUnPushedFile(fileName: String) {
        val currentSet = getUnPushedFiles()?.toMutableSet()
        currentSet?.add(fileName)
        prefs.edit().putStringSet(key, currentSet).apply()
        Log.i(
            "FireBase Repository",
            "The file ($fileName) has been added to the queue. Current queue size: ${currentSet?.size}"
        )
    }

    fun deleteUnPushedFile(fileName: String) {
        val currentSet = getUnPushedFiles()?.toMutableSet()
        currentSet?.remove(fileName)
        prefs.edit().putStringSet(key, currentSet).apply()
        Log.i(
            "FireBase Repository",
            "The file ($fileName) has been removed to the queue. Current queue size: ${currentSet?.size}"
        )
    }

    fun deleteQueue() {
        prefs.edit().putStringSet(key, setOf<String>()).apply()
        Log.i("FireBase Repository", "Deleted all files in queue.")
    }
}