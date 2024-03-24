package com.example.activity_tracker.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket

interface IBluetoothEventListener {
    fun onEnabled()
    fun onDisable()
    fun onDiscovery()
    fun onDiscovered(devices: Set<BluetoothDevice>)
    fun onDiscovered(device: BluetoothDevice)
    fun onPaired(device: BluetoothDevice)
    fun onConnected(socket: BluetoothSocket)
    fun onDisconnected()
}