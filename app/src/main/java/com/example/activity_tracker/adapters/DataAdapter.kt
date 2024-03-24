package com.example.activity_tracker.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.activity_tracker.FileManager
import com.example.activity_tracker.R
import com.example.activity_tracker.application.DetailActivity
import com.example.activity_tracker.repositories.FireBaseRepository
import kotlinx.android.synthetic.main.item_data.view.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class DataAdapter(private val files: List<String>) :
    RecyclerView.Adapter<DataAdapter.ViewHolder>(), KoinComponent {
    private val ctx: Context by inject()
    private val fileManager = FileManager(ctx)
    private val fireBaseRepository: FireBaseRepository by inject()

    override fun getItemCount() = files.size

    override fun onCreateViewHolder(view: ViewGroup, position: Int) =
        ViewHolder(
            LayoutInflater.from(view.context).inflate(
                R.layout.item_data,
                view,
                false
            )
        )


    override fun onBindViewHolder(view: ViewHolder, position: Int) {
        view.itemView.ShareData.setOnClickListener {
            fileManager.shareData(files[view.adapterPosition])
        }
        view.itemView.setOnLongClickListener {
            fileManager.shareData(files[view.adapterPosition])
            true
        }

        view.itemView.setOnClickListener {
            val detailIntent = Intent(ctx, DetailActivity::class.java)
            detailIntent.putExtra("fileFullName", files[view.adapterPosition])
            detailIntent.putExtra("fileShortName", view.dataName?.text ?: "No name")
            detailIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            ctx.startActivity(detailIntent)
        }

        if (fireBaseRepository.getUnPushedFiles()?.contains(files[position]) != true)
            view.dataImage?.setImageResource(R.drawable.ic_pushed)
        else
            view.dataImage?.setImageResource(R.drawable.ic_not_pushed)

        view.dataName?.text = files[position].split("||")[0]
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), KoinComponent {
        val dataName: TextView? = itemView.DataName
        val dataImage: ImageView? = itemView.StatusData
    }
}
