package com.example.activity_tracker.adapters

import android.content.Context
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.activity_tracker.SwipeToDeleteCallback

class SwipeToDeleteHelper(private val context: Context, private val recyclerView: RecyclerView) {
    private val itemTouchHelperCallback =
        object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, position: Int) {
                (recyclerView.adapter as ActivitiesAdapter).removeItem(viewHolder)
            }

            override fun onMove(
                p0: RecyclerView,
                p1: RecyclerView.ViewHolder,
                p2: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }
        }

    private val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)

    fun attach() {
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}