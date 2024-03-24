package com.example.activity_tracker.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.activity_tracker.event_bus.DeviceFoundEvent
import com.example.activity_tracker.event_bus.FoundNewDevice
import com.example.activity_tracker.repositories.BluetoothRepository
import org.greenrobot.eventbus.EventBus
import org.koin.core.KoinComponent
import org.koin.core.inject


class BluetoothDiscovering : BroadcastReceiver(), KoinComponent {
    private val devicesSet = mutableSetOf<ArrayList<String>>()
    private val bluetoothRepository: BluetoothRepository by inject()

    private fun checkDevice(device: BluetoothDevice?) {
        if (bluetoothRepository.getLastDevice()?.address == device?.address) {
            EventBus.getDefault().post(DeviceFoundEvent(device))
            Log.i("BluetoothDiscovering", "Last device founded!")
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action ?: return
        if (BluetoothDevice.ACTION_FOUND == action) {
            val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
            devicesSet.add(arrayListOf(device?.name ?: device?.address ?: "null", device?.address ?: "null"))
            EventBus.getDefault().post(FoundNewDevice())
            checkDevice(device)
            Log.i("BluetoothDiscovering", "Device found: " + device?.name + "; MAC " + device?.address)
        }
    }

    fun getDevices() = devicesSet.toTypedArray()

}

