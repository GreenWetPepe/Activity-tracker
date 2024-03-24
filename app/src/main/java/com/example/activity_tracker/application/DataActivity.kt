package com.example.activity_tracker.application

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.activity_tracker.FileManager
import com.example.activity_tracker.R
import com.example.activity_tracker.adapters.DataAdapter
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DataActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data)

        val files = FileManager(applicationContext).getFileNames()
        Log.i("Data", "$files")

        files.sortWith(compareBy { it.substring(0..10) })
        files.sortDescending()

        val recyclerView: RecyclerView = findViewById(R.id.dataRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = DataAdapter(files)
        Log.i("Data", "OnCreated")
    }
}