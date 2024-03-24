package com.example.activity_tracker.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import com.example.activity_tracker.event_bus.*
import org.greenrobot.eventbus.EventBus

class BluetoothEventListener : IBluetoothEventListener {

    override fun onConnected(socket: BluetoothSocket) {
        EventBus.getDefault().post(SocketOpenedEvent(socket))
    }

    override fun onDisconnected() {
        EventBus.getDefault().post(DeviceDisconnected())
        EventBus.getDefault().post(StopSession())
    }

    override fun onPaired(device: BluetoothDevice) {
        EventBus.getDefault().post(DevicePairedEvent(device))
    }

    override fun onDiscovered(device: BluetoothDevice) {
        EventBus.getDefault().post(DeviceDiscoveredEvent(device))
    }

    override fun onDiscovery() {
        EventBus.getDefault().post(OnDiscoveryEvent())
    }

    override fun onDiscovered(devices: Set<BluetoothDevice>) {
        EventBus.getDefault().post(DiscoveryFinishedEvent(devices))

    }

    override fun onEnabled() {
        EventBus.getDefault().post(BTEnabledEvent())
    }

    override fun onDisable() {
        EventBus.getDefault().post(BTDisabledEvent())
    }
}