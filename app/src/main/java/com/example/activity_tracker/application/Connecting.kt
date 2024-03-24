package com.example.activity_tracker.application

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.activity_tracker.R
import com.example.activity_tracker.adapters.ConnectionAdapter
import com.example.activity_tracker.bluetooth.BluetoothDiscovering
import com.example.activity_tracker.event_bus.FoundNewDevice
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.toast
import org.koin.core.KoinComponent
import org.koin.core.inject

class Connecting : Fragment(), KoinComponent {
    private val ctx: Context by inject()
    private val bluetoothDiscovering = BluetoothDiscovering()
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val REQUEST_ENABLE_BT = 1
    private lateinit var fragmentView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (bluetoothAdapter == null)
            context?.toast("Device doesn't support Bluetooth")

        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        ctx.registerReceiver(bluetoothDiscovering, filter)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v: View = inflater.inflate(R.layout.fragment_connecting, container, false)
        fragmentView = v

        v.findViewById<ImageView>(R.id.ic_refresh).setOnClickListener {
            updateRecycler(v)
        }

        updateRecycler(v)
        Handler().postDelayed({
            updateRecycler(v)
        }, 5000)
        return v
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        ctx.registerReceiver(bluetoothDiscovering, filter)
        EventBus.getDefault().register(this)
        Log.i("Connecting", "onStart")
    }

    override fun onStop() {
        super.onStop()
        ctx.unregisterReceiver(bluetoothDiscovering)
        Log.i("Connecting", "onStop")
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun handlerFoundEvent(ev: FoundNewDevice) {
        Log.i("Connecting", "Founded new device!")
        if (::fragmentView.isInitialized)
            updateRecycler(fragmentView)
    }

    private fun handlerBTIntent() {
        Log.i("Connecting", "Searching new devices...")
        if (bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter.cancelDiscovery()
        }
        bluetoothAdapter?.startDiscovery()
    }

    private fun updateRecycler(v: View) {
        handlerBTIntent()
        val devices = bluetoothDiscovering.getDevices()
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerViewConnection)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = ConnectionAdapter(devices)
        Log.i("Connecting", "Recycler view has been changed.")
    }
}



