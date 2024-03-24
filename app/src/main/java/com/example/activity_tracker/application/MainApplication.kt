package com.example.activity_tracker.application

import MotionRepository
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.activity_tracker.FileManager
import com.example.activity_tracker.SocketState
import com.example.activity_tracker.StateSwitcher
import com.example.activity_tracker.StepMeter
import com.example.activity_tracker.bluetooth.*
import com.example.activity_tracker.graphics.CarefulGraphic
import com.example.activity_tracker.repositories.ApplicationRepository
import com.example.activity_tracker.repositories.BluetoothRepository
import com.example.activity_tracker.repositories.FireBaseRepository
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.util.*

val BT_APP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")

class MainApplication : Application() {
    public val CHANNEL_ID: String = "bluetoothServiceChannel"

    override fun onCreate() {
        super.onCreate()

        val socketState = SocketState()

        val dataBaseModule = module {
            single { MotionRepository(applicationContext) }
            single { BluetoothRepository(applicationContext) }
            single { ApplicationRepository(applicationContext) }
            single { FireBaseRepository(applicationContext) }
        }
        val bluetoothModule = module {
            single<IBluetoothEventListener> { BluetoothEventListener() }
            single { ConnectRequest(get()) }
            single { ListenRequest(get()) }
            single { BluetoothClient() }
            single { socketState }

        }
        val dataProcessingModule = module {
            single { CarefulGraphic() }
            single { StateSwitcher() }
            single { FileManager(get()) }
            single { StepMeter() }
        }

        startKoin {
            androidContext(this@MainApplication)
            modules(listOf(dataBaseModule, dataProcessingModule, bluetoothModule))
        }
//        val bluetooth: BluetoothClient by inject()
        val motionRepository: MotionRepository by inject()
        val applicationRepository: ApplicationRepository by inject()
        val fileManager: FileManager by inject()

        fileManager.pushRemainingFiles()

        if (applicationRepository.firstLaunch)
            motionRepository.defaultMotions()

//        EventBus.getDefault().register(bluetooth)
        EventBus.getDefault().register(fileManager)
        applicationRepository.firstLaunch = false
        applicationContext.startService(Intent(applicationContext, BluetoothService::class.java))
        Log.i("Application", "onCreated.")
    }

    override fun onTerminate() {
        super.onTerminate()
//        val bluetooth: BluetoothClient by inject()
        val fileManager: FileManager by inject()
//        EventBus.getDefault().unregister(bluetooth)
        EventBus.getDefault().unregister(fileManager)
    }
}