package com.example.activity_tracker.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.activity_tracker.Motion
import com.example.activity_tracker.R
import com.example.activity_tracker.StateSwitcher
import com.example.activity_tracker.event_bus.SwitchState
import kotlinx.android.synthetic.main.state_recording.view.*
import org.greenrobot.eventbus.EventBus
import org.koin.core.KoinComponent
import org.koin.core.inject

class TrainAdapter(private val motions: ArrayList<Motion>, private val context: Context) :
    RecyclerView.Adapter<TrainAdapter.ViewHolder>(), KoinComponent {

    override fun getItemCount() = motions.size

    override fun onCreateViewHolder(view: ViewGroup, position: Int) = ViewHolder(
        LayoutInflater.from(view.context).inflate(
            R.layout.state_recording,
            view,
            false
        )
    )

    override fun onBindViewHolder(view: ViewHolder, position: Int) {
        view.bind(motions[position].name, motions[position].tag.toString())
        view.itemView.setOnClickListener {
            EventBus.getDefault().post(SwitchState(motions[position], position))
            view.switchStateColor(context = context)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), KoinComponent {
        private val motionName: TextView? = itemView.StateMotionName
        private val motionImg: ImageView? = itemView.StateMotionImg
        private val motionBox: ImageView? = itemView.StateMotionBox
        private val buttonMode: StateSwitcher by inject()
        private val ctx: Context by inject()

        fun bind(motion: String, tag: String) {
            val resID = ctx.resources.getIdentifier(tag, "drawable", ctx.packageName)
            switchStateColor(this, ctx)
            motionImg?.setImageResource(resID)
            motionName?.text = motion
        }

        fun switchStateColor(view: ViewHolder = this, context: Context) {
            if (buttonMode.getPosition() == view.adapterPosition)
                changeMotionColor(view, context.resources.getColor(R.color.salmonColor), 1f)
            else
                changeMotionColor(view, context.resources.getColor(R.color.clickItem), 0.3f)
        }

        private fun changeMotionColor(view: ViewHolder, color: Int, alpha: Float) {
            view.motionImg?.setColorFilter(color)
            view.motionImg?.alpha = alpha
            view.motionName?.setTextColor(color)
            view.motionBox?.setColorFilter(color)
            view.motionBox?.alpha = alpha
        }
    }
}
