package com.example.activity_tracker.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Handler
import android.util.Log
import com.example.activity_tracker.SocketState
import com.example.activity_tracker.event_bus.*
import com.example.activity_tracker.repositories.BluetoothRepository
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.core.KoinComponent
import org.koin.core.inject

class BluetoothClient : KoinComponent {
    private val handler = Handler()
    private val listenRequest: ListenRequest by inject()
    private val connectRequest: ConnectRequest by inject()
    private val blRepository: BluetoothRepository by inject()
    private val socketState: SocketState by inject()
    private var canTryAgain = true


    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onStartWithConnecting(ev: DeviceFoundEvent) {
        if (ev.device == null) return
        Log.i("BluetoothClient", "onStartWithConnecting: Device ${ev.device.name} connecting.")
        connectDevice(ev.device)
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun deleteLastDevice(ev: DeleteLastDevice) {
        blRepository.deleteLastDevice()
        Log.i("BluetoothClient", "Deleted last device.")
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onPaired(ev: DevicePairedEvent) {
        Log.i("BluetoothClient", "onPaired: Device ${ev.device.name} is ready for connection.")
        connectDevice(ev.device)
    }

    private fun connectDevice(device: BluetoothDevice) {
        Log.i("BluetoothClient", "Connecting with ${device.name}...")
        connectRequest.start(device)
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onSocketOpened(ev: SocketOpenedEvent) {
        handler.removeCallbacksAndMessages(null)
        socketState.setConnected()
        listenRequest.start(ev.socket)
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onDisconnected(ev: SocketClosedEvent) {
        Log.i("BluetoothClient", "Disconnected ${ev.socket?.remoteDevice}")
        socketState.setDisconnected()
        handler.postDelayed({ tryAgain() }, 3000)
    }

    fun onCreate(context: Context) {
        EventBus.getDefault().register(this)
        handler.removeCallbacksAndMessages(null)
    }

    fun onDestroy(context: Context) {
        EventBus.getDefault().unregister(this)
        handler.removeCallbacksAndMessages(null)
        canTryAgain = false

        connectRequest.onDestroy()
        listenRequest.onDestroy()
        socketState.setDisconnected()
    }


    private fun tryAgain() {
        handler.removeCallbacksAndMessages(null)
        if (!canTryAgain) return
        Log.i("BluetoothClient", "Try again!")
    }
}
