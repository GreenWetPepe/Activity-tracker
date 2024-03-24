package com.example.activity_tracker.application

import android.app.Activity
import android.app.AlertDialog
import com.example.activity_tracker.R
import com.example.activity_tracker.event_bus.DeleteMeasurements
import org.greenrobot.eventbus.EventBus

class DialogDeleteMeasurements(private val activity: Activity) {

    fun createDialog() {
        val inflater = activity.layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_delete_measurements, null)
        val addActivity = AlertDialog.Builder(activity).setView(dialogLayout)

        addActivity.setPositiveButton("OK")
        { _, _ -> EventBus.getDefault().post(DeleteMeasurements()) }

        addActivity.setNegativeButton("CANCEL")
        { dialog, _ -> dialog.cancel() }

        addActivity.show().window?.setBackgroundDrawableResource(R.drawable.dialog_small)
    }
}