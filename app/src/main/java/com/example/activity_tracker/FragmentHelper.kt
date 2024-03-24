package com.example.activity_tracker

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.activity_tracker.application.Connecting
import com.example.activity_tracker.application.Profile
import com.example.activity_tracker.application.Train
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.core.KoinComponent
import org.koin.core.inject

class FragmentHelper(private val activity: FragmentActivity) : KoinComponent {
    val train = Train()
    val connecting = Connecting()
    private val profile = Profile()
    private val socketState: SocketState by inject()


    val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                openHome()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {
                openFragment(profile)
                Log.i("FragmentHelper", "Opened Profile")
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    fun openFragment(fragment: Fragment) {
        if (fragment.isVisible)
            return

        val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }

    fun openHome() {
//        if (!socketState.isConnected()) {
//            openFragment(connecting)
//        } else
            openFragment(train)
        Log.i("FragmentHelper", "Opened Home")
    }

    fun isHome() = train.isVisible || connecting.isVisible
}