package com.example.activity_tracker.event_bus

import com.example.activity_tracker.Motion

class CallToLoadPoints
class StopSession
class DeleteMeasurements
class OpenTrain

class UpdateStepCounterRL(
    val steps: Int
)

class SwitchState(
    val motion: Motion,
    val position: Int
)

class CreateSession(
    val fileName: String,
    val motion: Motion,
    val date: String,
    val userName: String
)

data class DataEvent(
    val x: Int,
    val y: Int,
    val z: Int
)

data class LoadPoints(
    val listX: List<Int>,
    val listY: List<Int>,
    val listZ: List<Int>
)

data class DataSavePoint(
    val x: Int,
    val y: Int,
    val z: Int
)

data class DataFile(
    val graphList: MutableList<Int>,
    val motion: Motion,
    val time: String,
    val fileName: String,
    val userName: String
)