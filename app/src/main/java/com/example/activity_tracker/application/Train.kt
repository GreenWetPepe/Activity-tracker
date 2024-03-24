package com.example.activity_tracker.application

import MotionRepository
import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.activity_tracker.R
import com.example.activity_tracker.adapters.TrainAdapter
import com.example.activity_tracker.event_bus.CallToLoadPoints
import com.example.activity_tracker.event_bus.UpdateStepCounterRL
import com.example.activity_tracker.graphics.Graphic
import com.example.activity_tracker.graphics.GraphicService
import com.example.activity_tracker.options.GraphicOptions
import com.example.activity_tracker.repositories.ApplicationRepository
import com.jjoe64.graphview.GraphView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.find
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class Train : Fragment(), KoinComponent {
    private val ctx: Context by inject()
    private val motionRepository: MotionRepository by inject()
    private lateinit var grX: Graphic
    private lateinit var grY: Graphic
    private lateinit var grZ: Graphic
    private lateinit var stepCounterRL: TextView
    private lateinit var tflite: Interpreter

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(grX)
        EventBus.getDefault().register(grY)
        EventBus.getDefault().register(grZ)
        EventBus.getDefault().register(this)
        EventBus.getDefault().post(CallToLoadPoints())
        Log.i("Train", "Resumed")
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(grX)
        EventBus.getDefault().unregister(grY)
        EventBus.getDefault().unregister(grZ)
        EventBus.getDefault().unregister(this)
        Log.i("Train", "Paused")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)

        val v: View = inflater.inflate(R.layout.fragment_graphics, container, false)
        val motions = motionRepository.getMotions()
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerStateMotions)
        val graphX = v.findViewById<GraphView>(R.id.graphX)
        val graphY = v.findViewById<GraphView>(R.id.graphY)
        val graphZ = v.findViewById<GraphView>(R.id.graphZ)
        val userName = v.findViewById<TextView>(R.id.userName)
        stepCounterRL = v.findViewById<TextView>(R.id.stepCounter)
        val graphicOptions = GraphicOptions()
        val applicationRepository: ApplicationRepository by inject()
        userName.text = applicationRepository.getCurrentUser()

        grX = Graphic(
            graphX,
            graphicOptions.max_X,
            graphicOptions.max_Y,
            graphicOptions.min_Y,
            { it.x },
            { it.listX })
        grY = Graphic(
            graphY,
            graphicOptions.max_X,
            graphicOptions.max_Y,
            graphicOptions.min_Y,
            { it.y },
            { it.listY })
        grZ = Graphic(
            graphZ,
            graphicOptions.max_X,
            graphicOptions.max_Y,
            graphicOptions.min_Y,
            { it.z },
            { it.listZ })

        recyclerView.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = TrainAdapter(motions, ctx)

        ctx.startService(Intent(ctx, GraphicService::class.java))
        tflite = Interpreter(loadModelFile())
        var prediction = doInference(arrayOf(arrayOf(12, 123, 432, 23, 423, 432, 42, 543, 22)))
        Log.i("ML", "Your motion is $prediction")
        Toast.makeText(ctx, "Your motion is $prediction", Toast.LENGTH_LONG).show()

        v.findViewById<ImageView>(R.id.createUser).setOnClickListener {
            val activity = activity
            if (activity != null)
                DialogCreateUser(activity).createDialog(userName)
        }
        Log.i("Train", "Created View.")
        return v
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateStepCountRL(ev: UpdateStepCounterRL) {
//        Log.i("StepCounterRL", "SendSteps")
        stepCounterRL.text = "Steps: ${ev.steps}"
    }

    public fun doInference(array: Array<Array<Int>>): Int? {
        var outputVal: Int? = null
        tflite.run(array, outputVal)
        return outputVal
    }
    @Throws(IOException::class)
    private fun loadModelFile(): MappedByteBuffer {
        val fileDescript: AssetFileDescriptor = ctx.assets.openFd("MotionML.h5")
        val inputStream = FileInputStream(fileDescript.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescript.startOffset
        val declarLeight = fileDescript.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declarLeight)
    }
}
