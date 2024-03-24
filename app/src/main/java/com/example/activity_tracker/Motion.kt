package com.example.activity_tracker

import MotionRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class Motion(val name: String, val tag: Any?) : KoinComponent {
    fun isExist(): Boolean {
        val motionRepository: MotionRepository by inject()
        val motions = motionRepository.getMotions()
        for (motion in motions) {
            if (motion.name == this.name)
                return true
        }
        return false
    }
}