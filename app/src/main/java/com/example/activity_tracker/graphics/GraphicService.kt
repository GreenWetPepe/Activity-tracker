package com.example.activity_tracker.graphics

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.activity_tracker.StateSwitcher
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.android.inject

class GraphicService : Service() {
    private val carefulGr: CarefulGraphic by inject()
    private val stateSwitcher: StateSwitcher by inject()

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(carefulGr)
        EventBus.getDefault().register(stateSwitcher)
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(carefulGr)
        EventBus.getDefault().unregister(stateSwitcher)
        super.onDestroy()
    }
}
