package com.example.activity_tracker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.activity_tracker.R
import com.example.activity_tracker.bluetooth.PairRequest
import com.example.activity_tracker.repositories.BluetoothRepository
import kotlinx.android.synthetic.main.item_device.view.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class ConnectionAdapter(private val devices: Array<ArrayList<String>>) :
    RecyclerView.Adapter<ConnectionAdapter.ViewHolder>(), KoinComponent {
    private val bluetoothRepository: BluetoothRepository by inject()

    override fun getItemCount() = devices.size

    override fun onCreateViewHolder(view: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(view.context).inflate(
                R.layout.item_device,
                view,
                false
            )
        )
    }

    override fun onBindViewHolder(view: ViewHolder, position: Int) {
        view.deviceName?.text = devices[position][0]
        view.deviceAddress?.text = devices[position][1]

        view.deviceImage?.setImageResource(0)
        if (devices[position][0] in bluetoothRepository.supportedDevices) {
            view.deviceImage?.setImageResource(R.drawable.ic_device)
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), KoinComponent {
        private val pairRequest = PairRequest()
        val deviceName: TextView? = itemView.DeviceName
        val deviceAddress: TextView? = itemView.MacAddress
        val deviceImage: ImageView? = itemView.beat_image

        init {
            itemView.setOnClickListener {
                pairRequest.pairDevice(itemView.MacAddress.text.toString())
//                BluetoothAdapter.getDefaultAdapter().startDiscovery()
            }
        }
    }
}
