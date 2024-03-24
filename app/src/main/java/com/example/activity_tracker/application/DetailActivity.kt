package com.example.activity_tracker.application

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.activity_tracker.FileManager
import com.example.activity_tracker.PointLists
import com.example.activity_tracker.R
import com.example.activity_tracker.StepMeter
import com.example.activity_tracker.graphics.DetailActivityGraphics
import com.example.activity_tracker.options.GraphicOptions
import com.jjoe64.graphview.GraphView
import org.koin.android.ext.android.inject

class DetailActivity : AppCompatActivity() {
    private val stepMeter = StepMeter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val fileManager = FileManager(applicationContext)
        val fileFullName = intent.getStringExtra("fileFullName") ?: "None"
        val fileShortName = intent.getStringExtra("fileShortName") ?: "None"
        val graphicOptions = GraphicOptions()
        val graphX = findViewById<GraphView>(R.id.graphX)
        val graphY = findViewById<GraphView>(R.id.graphY)
        val graphZ = findViewById<GraphView>(R.id.graphZ)
        val parsedObject = fileManager.getDataInfo(fileFullName)
        val parsedPoints = parsedObject?.graphList ?: mutableListOf(0)
        val pointLists = parsePointList(parsedPoints)

        findViewById<TextView>(R.id.comment_detail).text =
            "It's been a long and wonderful day. Sakura leaves fell at a speed of 5 centimeters per second, while ${parsedObject?.userName} ${parsedObject?.motion?.name}."

        supportActionBar?.title = fileShortName


        findViewById<ConstraintLayout>(R.id.ShareDetailLayout).setOnClickListener {
            fileManager.shareData(fileFullName)
        }
        val grX = DetailActivityGraphics(
            graphX,
            graphY,
            graphZ,
            graphicOptions.max_X,
            graphicOptions.max_Y,
            graphicOptions.min_Y,
            pointLists.listX,
            applicationContext
        )
        val grY = DetailActivityGraphics(
            graphY,
            graphX,
            graphZ,
            graphicOptions.max_X,
            graphicOptions.max_Y,
            graphicOptions.min_Y,
            pointLists.listY,
            applicationContext
        )
        val grZ = DetailActivityGraphics(
            graphZ,
            graphX,
            graphY,
            graphicOptions.max_X,
            graphicOptions.max_Y,
            graphicOptions.min_Y,
            pointLists.listZ,
            applicationContext
        )
        if(parsedObject?.motion?.name == "Walk") {
            stepMeter.fillGraphs(pointLists.listX, pointLists.listY, pointLists.listZ)
            stepMeter.countSteps()
            stepMeter.cleanList()
        }
    }

    private fun parsePointList(allPoints: MutableList<Int>): PointLists {
        val listX = mutableListOf<Int>()
        val listY = mutableListOf<Int>()
        val listZ = mutableListOf<Int>()

        allPoints.forEachIndexed { index, point ->
            when {
                index % 3 == 0 -> listX.add(point)
                index % 3 == 1 -> listY.add(point)
                index % 3 == 2 -> listZ.add(point)
            }
        }
        return PointLists(listX, listY, listZ)
    }
}