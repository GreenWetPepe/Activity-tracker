package com.example.activity_tracker.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import com.example.activity_tracker.Device
import com.example.activity_tracker.application.BT_APP_UUID
import com.example.activity_tracker.repositories.BluetoothRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class ConnectRequest(private val listener: IBluetoothEventListener) {
    private var thread: ConnectThread? = null

    private fun stop() {
        thread?.cancel()
        thread = null
    }

    fun onDestroy() {
        stop()
    }

    fun start(device: BluetoothDevice) {
        thread?.cancel()
        Log.i("ConnectRequest", "start($device)")
        thread = ConnectThread(device, listener).apply {
            start()
        }
    }

    private class ConnectThread(
        private val device: BluetoothDevice,
        private val listener: IBluetoothEventListener
    ) : Thread(), KoinComponent {

        private val pref: BluetoothRepository by inject()
        private val socket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            try {
                device.createInsecureRfcommSocketToServiceRecord(BT_APP_UUID)
            } catch (e: Exception) {
                Log.e("ConnectThread", "Cannot create socket: ${e.message}", e)
                device.createInsecureRfcommSocketToServiceRecord(BT_APP_UUID)
                null
            }
        }

        override fun run() {
            try {
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                socket?.also { sock ->
                    sock.connect()
                    listener.onConnected(sock)
                    pref.addDevice(Device(device.address, device.name))
                    Log.i("BLUETOOTH", "Device(${device.address}) added to preferences.")
                }
            } catch (e: Exception) {
                Log.i("BLUETOOTH", "Cannot connect to socket ${socket?.isConnected}: ${e.message}")
                listener.onDisconnected()
            }
        }

        fun cancel() {
            try {
                if (socket?.isConnected == true) socket?.close()
            } catch (e: Exception) {
            } finally {
                listener.onDisconnected()
            }
        }
    }
}