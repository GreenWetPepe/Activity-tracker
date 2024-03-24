package com.example.activity_tracker.adapters

import MotionRepository
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.activity_tracker.Motion
import com.example.activity_tracker.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.item_motion.view.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class ActivitiesAdapter(private val motions: ArrayList<Motion>, private val context: Context) :
    RecyclerView.Adapter<ActivitiesAdapter.ViewHolder>(), KoinComponent {
    private val motionRepository: MotionRepository by inject()

    override fun getItemCount() = motions.size

    override fun onCreateViewHolder(view: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(view.context).inflate(
                R.layout.item_motion,
                view,
                false
            )
        )
    }

    override fun onBindViewHolder(view: ViewHolder, position: Int) {
        val resID = context.resources.getIdentifier(motions[position].tag.toString(), "drawable", context.packageName)
        view.motionName?.text = motions[position].name
        view.motionImg?.setImageResource(resID)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val motionName: TextView? = itemView.MotionName
        val motionImg: ImageView? = itemView.MotionImg
    }

    fun removeItem(viewHolder: RecyclerView.ViewHolder) {
        val removedItem: Motion = motions[viewHolder.adapterPosition]
        val removedItemName = removedItem.name
        val removedPosition = viewHolder.adapterPosition
        val snackBar = Snackbar.make(viewHolder.itemView, "$removedItemName deleted", Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view

        motionRepository.removeMotion(removedItemName)
        motions.removeAt(viewHolder.adapterPosition)
        notifyItemRemoved(viewHolder.adapterPosition)

        (snackBarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView).setTextColor(
            context.resources.getColor(
                R.color.salmonColor
            )
        )
        snackBar.setAction("UNDO") {
            motions.add(removedPosition, removedItem)
            motionRepository.addMotion(removedItemName, removedItem.tag.toString())
            notifyItemInserted(removedPosition)
        }
        snackBar.show()
    }

    fun insertItem(motion: Motion) {
        if (!motion.isExist())
            motions.add(motions.size, Motion(motion.name, motion.tag.toString()))
        motionRepository.addMotion(motion.name, motion.tag.toString())
        notifyDataSetChanged()
    }
}


