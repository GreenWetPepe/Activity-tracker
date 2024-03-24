package com.example.activity_tracker.application

import android.app.Activity
import android.app.AlertDialog
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.activity_tracker.Motion
import com.example.activity_tracker.R
import com.example.activity_tracker.adapters.ActivitiesAdapter
import kotlinx.android.synthetic.main.dialog_add.view.*

class DialogActivities(private val activity: Activity) {
    lateinit var preImgActivity: ImageView
    var selectedImgTag: String = "ac_planning"

    fun createDialog(recyclerView: RecyclerView) {
        val inflater = activity.layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_add, null)
        val addActivity = AlertDialog.Builder(activity).setView(dialogLayout)
        val activityName = dialogLayout.findViewById<EditText>(R.id.setActivityName)
        val activitiesAdapter = recyclerView.adapter as ActivitiesAdapter

        selectedImgTag = "ac_planning"
        preImgActivity = dialogLayout.selectedIcon

        addActivity.setPositiveButton("ADD")
        { _, _ ->
            if (activityName.length() == 0)
                return@setPositiveButton
            activitiesAdapter.insertItem(
                Motion(activityName.text.toString(), selectedImgTag)
            )
        }
        addActivity.setNegativeButton("CANCEL")
        { dialog, _ -> dialog.cancel() }

        addActivity.show().window?.setBackgroundDrawableResource(R.drawable.dialog)
    }
}