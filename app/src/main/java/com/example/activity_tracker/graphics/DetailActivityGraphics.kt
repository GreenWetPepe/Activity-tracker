package com.example.activity_tracker.graphics

import android.content.Context
import android.graphics.Color
import com.example.activity_tracker.R
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries

class DetailActivityGraphics(
    graphicMain: GraphView,
    graphicSecond: GraphView,
    graphicThird: GraphView,
    MAX_X: Double,
    MAX_Y: Double,
    MIN_Y: Double,
    pointList: List<Int>,
    ctx: Context
) {
    private val series = LineGraphSeries<DataPoint>()
    private var pointCounter = 0.0

    init {
        graphicMain.gridLabelRenderer.gridColor = Color.parseColor("#23949191")
        series.color = ctx.resources.getColor(R.color.salmonColor)

        graphicMain.viewport.isXAxisBoundsManual = true
        graphicMain.viewport.isYAxisBoundsManual = true
        graphicMain.viewport.setMaxX(MAX_X)
        graphicMain.viewport.onXAxisBoundsChangedListener

        graphicMain.viewport.setOnXAxisBoundsChangedListener { minX, maxX, _ ->
            graphicSecond.viewport.setMinX(minX)
            graphicSecond.viewport.setMaxX(maxX)
            graphicThird.viewport.setMinX(minX)
            graphicThird.viewport.setMaxX(maxX)
            graphicSecond.onDataChanged(true, true)
            graphicThird.onDataChanged(true, true)
        }

        graphicMain.viewport.setMaxY(MAX_Y)
        graphicMain.viewport.setMinY(MIN_Y)
        graphicMain.gridLabelRenderer.isHorizontalLabelsVisible = false
        graphicMain.gridLabelRenderer.isVerticalLabelsVisible = false
        graphicMain.viewport.isScrollable = true

        for (point in 0 until pointList.size) {
            val data = DataPoint(pointCounter, pointList[point].toDouble())
            series.appendData(data, true, pointList.size)
            pointCounter += 1.0
        }
        pointCounter++
        graphicMain.addSeries(series)
    }
}
