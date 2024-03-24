package com.example.activity_tracker.repositories

import android.content.Context
import android.content.SharedPreferences
import com.example.activity_tracker.Device

class BluetoothRepository(context: Context) {
    val supportedDevices: Set<String> = setOf("MI Band 2", "Mi Band 3", "SMARTSHOE_001")
    private val prefs: SharedPreferences = context.getSharedPreferences("com.activity.bluetooth", 0)

    fun addDevice(device: Device) {
        prefs.edit().putString("address", device.address).apply()
        prefs.edit().putString("name", device.name).apply()
    }

    fun deleteLastDevice() {
        prefs.edit().putString("address", "").apply()
        prefs.edit().putString("name", "").apply()
    }

    fun getLastDevice(): Device? {
        val address = prefs.getString("address", null) ?: return null
        val name = prefs.getString("name", null) ?: return null
        return Device(address, name)
    }
}