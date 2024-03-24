package com.example.activity_tracker

class SocketState(private var state: Boolean = false) {
    fun setDisconnected() {
        state = false
    }

    fun setConnected() {
        state = true
    }

    fun isConnected() = state
}