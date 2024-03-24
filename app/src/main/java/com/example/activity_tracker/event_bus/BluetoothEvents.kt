package com.example.activity_tracker.event_bus

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import java.io.Serializable

class BTEnabledEvent : Serializable
class BTDisabledEvent : Serializable
class OnDiscoveryEvent : Serializable

class DiscoveryFinishedEvent(
    val devices: Set<BluetoothDevice>
) : Serializable

class DeviceDiscoveredEvent(
    val device: BluetoothDevice
) : Serializable

class DevicePairedEvent(
    val device: BluetoothDevice
) : Serializable

class SocketOpenedEvent(
    val socket: BluetoothSocket
) : Serializable

class SocketClosedEvent(
    val socket: BluetoothSocket?
) : Serializable

data class DeviceFoundEvent(
    val device: BluetoothDevice?
)

class DeleteLastDevice
class DeviceDisconnected
class FoundNewDevice