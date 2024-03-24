package com.example.activity_tracker.graphics

import com.example.activity_tracker.event_bus.CallToLoadPoints
import com.example.activity_tracker.event_bus.DataEvent
import com.example.activity_tracker.event_bus.LoadPoints
import com.example.activity_tracker.options.GraphicOptions
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class CarefulGraphic {
    private val listX = mutableListOf<Int>()
    private val listY = mutableListOf<Int>()
    private val listZ = mutableListOf<Int>()
    private val graphicOptions = GraphicOptions()

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun savePoint(ev: DataEvent) {
        listX.add(ev.x)
        listY.add(ev.y)
        listZ.add(ev.z)
        if (listX.size > graphicOptions.pointsToML) {
            listX.removeAt(0)
            listY.removeAt(0)
            listZ.removeAt(0)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun sendPoint(ev: CallToLoadPoints) {
        EventBus.getDefault().post(LoadPoints(listX, listY, listZ))
    }
}