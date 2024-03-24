package com.example.activity_tracker.bluetooth

import android.bluetooth.BluetoothAdapter
import android.os.Build
import android.util.Log
import com.example.activity_tracker.event_bus.DevicePairedEvent
import org.greenrobot.eventbus.EventBus
import org.koin.core.KoinComponent

class PairRequest : KoinComponent {
    fun pairDevice(mac: String) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            val device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mac)
            Log.i("PairRequest", "Pairing...")
            device.createBond()
            Log.i("PairRequest", "Device paired.")
            EventBus.getDefault().post(
                DevicePairedEvent(device)
            )
        }
    }
}