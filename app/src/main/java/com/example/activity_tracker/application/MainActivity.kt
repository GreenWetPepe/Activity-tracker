package com.example.activity_tracker.application

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.activity_tracker.FragmentHelper
import com.example.activity_tracker.PermissionsHelper
import com.example.activity_tracker.R
import com.example.activity_tracker.event_bus.DeviceDisconnected
import com.example.activity_tracker.event_bus.OpenTrain
import com.example.activity_tracker.event_bus.SocketOpenedEvent
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MainActivity : AppCompatActivity() {
    private val fragmentHelper = FragmentHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val permissionsHelper = PermissionsHelper(this)

        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(fragmentHelper.onNavigationItemSelectedListener)

        permissionsHelper.allowLocation()
        fragmentHelper.openHome()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onConnected(ev: SocketOpenedEvent) {
        Log.i("MainActivity", "onConnected")
        if (fragmentHelper.isHome())
            fragmentHelper.openFragment(fragmentHelper.train)
    }

    @Subscribe
    fun onDisconnected(ev: DeviceDisconnected) {
        if (fragmentHelper.isHome())
            fragmentHelper.openFragment(fragmentHelper.connecting)
    }

    @Subscribe
    fun openTrain(ev: OpenTrain) {
        if(!fragmentHelper.train.isResumed && fragmentHelper.isHome()) fragmentHelper.openFragment(fragmentHelper.train)
    }
}
