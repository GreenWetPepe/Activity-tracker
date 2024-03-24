package com.example.activity_tracker

import android.util.Log
import kotlin.math.abs

class StepMeterRL {
    private val stepEnd = 255 * 80 / 100
    private val stepGo = 220
    private var stepPhase = false
    private var miss = 0
    private var steps = 0

    fun getSteps() = steps

    fun clearSteps() {
        steps = 0
    }

    fun isStep(x: Int, y: Int, z: Int) {
        if (x < stepEnd) {
            stepPhase = true
        }
        if (miss <= 0) {
            if (abs(z) > stepGo && x > stepEnd && stepPhase) {
                steps++
                stepPhase = false
                miss = 10
            }
        }
        miss--
    }
//        Log.i("X_Value", x.toString())
    }