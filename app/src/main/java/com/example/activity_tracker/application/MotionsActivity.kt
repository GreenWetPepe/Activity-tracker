package com.example.activity_tracker.application

import MotionRepository
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.activity_tracker.R
import com.example.activity_tracker.adapters.ActivitiesAdapter
import com.example.activity_tracker.adapters.SwipeToDeleteHelper
import kotlinx.android.synthetic.main.activity_activities.*
import org.koin.android.ext.android.inject

class MotionsActivity : AppCompatActivity() {
    private val motionRepository: MotionRepository by inject()
    private val motions = motionRepository.getMotions()
    private val dialogActivities = DialogActivities(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_activities)

        val recyclerView: RecyclerView = findViewById(R.id.ActivityItems)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ActivitiesAdapter(motions, applicationContext)

        try {
            SwipeToDeleteHelper(applicationContext, recyclerView).attach()
        } catch (e: Exception) {
            Log.e("MotionsActivity", e.toString())
        }

        addActivityButton.setOnClickListener {
            dialogActivities.createDialog(recyclerView)
        }
    }

    fun clickEvent(v: View) {
        val resID = this.resources.getIdentifier(v.tag.toString(), "drawable", this.packageName)
        dialogActivities.preImgActivity.setImageResource(resID)
        dialogActivities.selectedImgTag = v.tag.toString()
    }
}
