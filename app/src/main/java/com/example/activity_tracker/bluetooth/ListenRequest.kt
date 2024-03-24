package com.example.activity_tracker.bluetooth

import android.bluetooth.BluetoothSocket
import android.util.Log
import com.example.activity_tracker.Coordinates
import com.example.activity_tracker.Parser
import com.example.activity_tracker.PayloadParser
import com.example.activity_tracker.event_bus.DataEvent
import com.example.activity_tracker.event_bus.DeviceDisconnected
import com.example.activity_tracker.event_bus.OpenTrain
import com.example.activity_tracker.event_bus.SocketClosedEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.core.KoinComponent
import java.util.*

class ListenRequest(private val listener: IBluetoothEventListener) {
    private var thread: ListenerThread? = null

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun start(socket: BluetoothSocket) {
        thread?.cancel()
        thread = ListenerThread(socket, listener, PayloadParser(), Parser()).apply {
            start()
        }
    }

    private fun stop() {
        thread?.cancel()
        thread = null
    }

    fun onDestroy() {
        stop()
    }

    private class ListenerThread(
        private val socket: BluetoothSocket,
        private val listener: IBluetoothEventListener,
        private val payloadParser: PayloadParser,
        private val parser: Parser
    ) : Thread(), KoinComponent {
        private val input = socket.inputStream
        private val buffer = ByteArray(1024)
        private var running = true
        private val coordQueue: Queue<Coordinates> = ArrayDeque()

        override fun run() {
            Log.i("Bluetooth", "Listening to ${socket.remoteDevice}")
            Log.i("Bluetooth", "Running State $running")
            while (canLoop()) {
                loop()
            }
            cancel()
        }

        fun cancel() {
            try {
                running = false
                if (socket.isConnected)
                    socket.close()
                Log.i("Bluetooth", "Socket Closed")
            } catch (e: Exception) {
                Log.i("Bluetooth", "Cannot close BT socket $socket for device ${socket.remoteDevice}: ${e.message}")
            } finally {
                listener.onDisconnected()
            }
        }

        private fun canLoop(): Boolean = running && socket.isConnected

        private fun loop() {
            try {
                val size = input.read(buffer)
                parser.parse(buffer, size).forEach { line ->
                    payloadParser.parse(line).forEach { coord ->
                        if (!canLoop()) return
                        pushCoordinates(coord)
                    }
                }
//                Log.i("Bluetooth", "LOOPING")
                EventBus.getDefault().post(OpenTrain())
            } catch (e: Exception) {
                EventBus.getDefault().post(SocketClosedEvent(socket))
                EventBus.getDefault().post(DeviceDisconnected())
                Log.i("BLUETOOTH", "Cannot read BT socket ${socket.remoteDevice}: ${e.message}", e)
                running = false
            }
        }

        private fun pushCoordinates(coordinates: Coordinates) {
            coordQueue.add(coordinates)
            if (coordQueue.size > 10) {
                val dataEvent = DataEvent(
                    coordQueue.map { it.x }.average().toInt(),
                    coordQueue.map { it.y }.average().toInt(),
                    coordQueue.map { it.z }.average().toInt()
                )
                EventBus.getDefault().post(dataEvent)
//                Log.i("Bluetooth", dataEvent.toString())
                coordQueue.clear()
            }
        }
    }
}