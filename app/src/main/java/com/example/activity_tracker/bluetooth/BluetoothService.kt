package com.example.activity_tracker.bluetooth

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.activity_tracker.application.MainApplication
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.android.inject
import java.nio.channels.Channel as Channel1

class BluetoothService : Service() {
    private val bt: BluetoothClient by inject()
    private lateinit var notifManager: NotificationManager

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()
//        isRecording = false
//        bt.onCreate(this)
        EventBus.getDefault().register(bt)
        notifManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onDestroy() {
        notifManager.cancelAll()
        super.onDestroy()
//        isRecording = false
//        bt.onDestroy(this)
        EventBus.getDefault().unregister(bt)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notIntent = Intent(this, MainApplication::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, notIntent, 0)

        val notification: Notification? = NotificationCompat.Builder(this, "bluetoothServiceChannel")
            .setContentTitle("Bluetooth Service")
            .setContentText("Touch to open")
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()
//        when (intent?.action) {
//            ACTION_STOP -> {
//                stopSelf()
//            }
//            ACTION_START_RECORDING -> {
//                isRecording = true
//            }
//            ACTION_STOP_RECORDING -> {
//                isRecording = false
//            }
//        }
//        displayNotificationMessage()
//        startForeground(25, notification)
        return START_STICKY
    }

    private fun displayNotificationMessage() {
//        notifManager.notify(25, notification)
    }


    companion object {
        const val ACTION_STOP = "it.sevenbits.shoe.ACTION_STOP_BT"
        const val ACTION_START = "it.sevenbits.shoe.ACTION_START_BT"
        const val ACTION_START_RECORDING = "it.sevenbits.shoe.ACTION_START_RECORDING"
        const val ACTION_STOP_RECORDING = "it.sevenbits.shoe.ACTION_STOP_RECORDING"

        var isRecording: Boolean = false
            private set
    }
}