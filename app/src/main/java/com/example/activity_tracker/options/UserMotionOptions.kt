package com.example.activity_tracker.options

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UserMotionOptions(
    userName: String,
    motion: String,
    max_X: Int,
    max_Y: Int,
    max_Z: Int
) {
    
    private val userName = userName
    private val motion = motion
    private val max_X = max_X
    private val max_Y = max_Y
    private val max_Z = max_Z
    fun sendToDataBase() {
        val dataBase = FirebaseDatabase.getInstance().getReference().child("$userName")
    }
}