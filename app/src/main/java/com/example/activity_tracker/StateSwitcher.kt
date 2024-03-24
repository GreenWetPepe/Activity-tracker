package com.example.activity_tracker

import android.util.Log
import com.example.activity_tracker.event_bus.*
import com.example.activity_tracker.repositories.ApplicationRepository
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.text.SimpleDateFormat
import java.util.*

class StateSwitcher : KoinComponent {
    private var currentState: SwitchState? = null
    private var isMotionStep = false
    private val format = SimpleDateFormat("yyyy_MM_dd-HH_mm_ss_SSS", Locale.getDefault())
    private val formatShort = SimpleDateFormat("dd.MM HH:mm", Locale.getDefault())
    private val stepMeter = StepMeter()
    private val stepMeterRL = StepMeterRL()

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun switchState(ev: SwitchState) {
        if(ev.motion.name == "Walk") isMotionStep = true
        when {
            currentState?.position == ev.position -> {
                EventBus.getDefault().post(StopSession())
                if(isMotionStep) {
                    stepMeter.countSteps()
                    stepMeter.cleanList()
                    stepMeterRL.clearSteps()
                    isMotionStep = false
                }
                currentState = null
                Log.i("StateSwitcher", "Stop Session!")
            }

            currentState == null -> {
                val fileName = "${formatShort.format(Date())}(${ev.motion.name})||"
                val appRepository: ApplicationRepository by inject()
                val userName = appRepository.getCurrentUser().toString()
                val session = CreateSession(fileName, ev.motion, format.format(Date()), userName)

                EventBus.getDefault().post(session)
                currentState = ev
                Log.i("switchState()", "Create Session!")
            }
        }
    }

    fun getPosition(): Int? {
        return currentState?.position
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun maybeSave(ev: DataEvent) {
        if (currentState != null) {
            EventBus.getDefault().post(DataSavePoint(ev.x, ev.y, ev.z))
            if(isMotionStep) {
                stepMeter.addPoint(ev.x, ev.y, ev.z)
                stepMeterRL.isStep(ev.x, ev.y, ev.z)
                EventBus.getDefault().post(UpdateStepCounterRL(stepMeterRL.getSteps()))
//                Log.i("StateSwitched", "SENDED")
            }
        }
    }
}