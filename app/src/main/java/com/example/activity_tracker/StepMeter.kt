package com.example.activity_tracker

import android.app.WallpaperInfo
import android.content.Context
import android.widget.Toast
import com.example.activity_tracker.event_bus.DataSavePoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.math.abs

class StepMeter : KoinComponent {
    private val ctx: Context by inject()
    private val stepConst = 50
    private val waitConst = 10
    var listX = mutableListOf<Int>()
    var listY = mutableListOf<Int>()
    var listZ = mutableListOf<Int>()

    fun countSteps() {
        var max_X = getMax(listX)
        var max_Y = getMax(listY)
        var max_Z = getMax(listZ)
        val stepX = max_X * stepConst / 100
        val waitX = max_X * waitConst / 100
        val stepY = max_Y * stepConst / 100
        val waitY = max_Y * waitConst / 100
        val stepZ = max_Z * stepConst / 100
        val waitZ = max_Z * waitConst / 100
        val choiceX = choiceStep(listX, stepX, waitX)
        val choiceY = choiceStep(listY, stepY, waitY)
        val choiceZ = choiceStep(listZ, stepZ, waitZ)
        val result = choiceZ

        Toast.makeText(ctx, "You making $result steps right leg", Toast.LENGTH_LONG).show()
//        Toast.makeText(ctx, "X: $choiceX, Y: $choiceY, Z: $choiceZ", Toast.LENGTH_LONG).show()
    }

    fun addPoint(x: Int, y: Int, z:Int) {
        listX.add(x)
        listY.add(y)
        listZ.add(z)
    }

    fun fillGraphs (list_X: List<Int>, list_Y: List<Int>, list_Z: List<Int>) {
        listX = list_X as MutableList<Int>
        listY = list_Y as MutableList<Int>
        listZ = list_Z as MutableList<Int>
    }

    fun getMax(list: List<Int>): Int {
        var max = 0
        for(point in list) {
            if (abs(point) > max) max = abs(point)
        }
        return max
    }

    fun choiceStep(list: List<Int>, step: Int, wait: Int): Int {
        var choice = 0
        var stepMaking = false
        for (point in list) {
            if(abs(point) >= step && !stepMaking) {
                choice++
                stepMaking = true
            }else if(abs(point) <= wait && stepMaking) {
                stepMaking = false
            }
        }
        return choice
    }

    fun cleanList() {
        listX.clear()
        listY.clear()
        listZ.clear()
    }
}