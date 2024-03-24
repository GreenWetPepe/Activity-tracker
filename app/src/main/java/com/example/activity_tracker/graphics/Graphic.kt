package com.example.activity_tracker.graphics

import android.content.Context
import android.graphics.Color
import com.example.activity_tracker.R
import com.example.activity_tracker.event_bus.DataEvent
import com.example.activity_tracker.event_bus.LoadPoints
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.core.KoinComponent
import org.koin.core.inject

class Graphic(
    graphic: GraphView,
    private val MAX_X: Double,
    MAX_Y: Double,
    MIN_Y: Double,
    val getter: (DataEvent) -> Int,
    val pointGetter: (LoadPoints) -> List<Int>
) : KoinComponent {
    private var point = 0.0
    private val series = LineGraphSeries<DataPoint>()
    private val ctx: Context by inject()

    init {
        graphic.addSeries(series)
        graphic.gridLabelRenderer.gridColor = Color.parseColor("#23949191")
        series.color = ctx.resources.getColor(R.color.salmonColor)

        graphic.viewport.isXAxisBoundsManual = true
        graphic.viewport.isYAxisBoundsManual = true
        graphic.viewport.setMaxX(MAX_X)
        graphic.viewport.setMaxY(MAX_Y)
        graphic.viewport.setMinY(MIN_Y)
        graphic.gridLabelRenderer.isHorizontalLabelsVisible = false
        graphic.gridLabelRenderer.isVerticalLabelsVisible = false
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun addPoint(ev: DataEvent) {
        val data = DataPoint(point, getter(ev).toDouble())
        point += 1
        series.appendData(data, true, MAX_X.toInt())
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun loadPoints(ev: LoadPoints) {
        for (i in 0 until pointGetter(ev).size) {
            val data = DataPoint(point, pointGetter(ev)[i].toDouble())
            point += 1
            series.appendData(data, true, MAX_X.toInt())
        }
    }
}