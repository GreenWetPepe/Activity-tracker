package com.example.activity_tracker

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.beust.klaxon.Klaxon
import com.example.activity_tracker.event_bus.*
import com.example.activity_tracker.repositories.FireBaseRepository
import com.google.firebase.storage.FirebaseStorage
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.Writer


class FileManager(private val ctx: Context) : KoinComponent {
    private val pointToSave = mutableListOf<Int>()
    private lateinit var filePath: String
    private lateinit var session: CreateSession
    private val mStorageRef = FirebaseStorage.getInstance()
    private val fireBaseRepository: FireBaseRepository by inject()
    private val extPath = ctx.getExternalFilesDir("/")

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

    fun pushRemainingFiles() {
        if (!isNetworkAvailable(ctx)) return
        val fireBaseRepository: FireBaseRepository by inject()
        val fileManager: FileManager by inject()

        fileManager.getFileNames().map {
            if (fireBaseRepository.getUnPushedFiles()?.contains(it) == true) {
                fileManager.uploadFile(it)
                fireBaseRepository.deleteUnPushedFile(it)
            }
        }
        Log.i("FileManager", "Pushed all remaining files.")
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun createSession(ev: CreateSession) {
        val file = createFile(ev.fileName)
        val fireBaseRepository: FireBaseRepository by inject()
        fireBaseRepository.addUnPushedFile(file.name)
        Log.i("FileManager", "Created file ${file.name}")
        filePath = "$extPath/${file.name}"
        session = CreateSession(file.name, ev.motion, ev.date, ev.userName)
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun handler(ev: DataSavePoint) {
        pointToSave.add(ev.x)
        pointToSave.add(ev.y)
        pointToSave.add(ev.z)
        if (pointToSave.size > 300) {
            putNewData()
            pointToSave.clear()
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun stopSession(ev: StopSession) {
        if (!::session.isInitialized) return
        Log.i("FileManager", "Session was stopped, data was recorded.")
        putNewData()
        uploadFile(session.fileName)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun deleteAllData(ev: DeleteMeasurements) {
        fireBaseRepository.deleteQueue()
        extPath?.deleteRecursively()
        Log.i("FileManager", "Deleted all data.")
    }

    private fun uploadFile(fileName: String) {
        val file = File("$extPath/$fileName")
        val uriFile = Uri.fromFile(file)
        val measRef = mStorageRef.reference.child("measurements/${file.nameWithoutExtension}.json")

        if (!isNetworkAvailable(ctx)) {
            fireBaseRepository.addUnPushedFile(fileName)
            return
        }

        measRef.putFile(uriFile)
            .addOnFailureListener {
                Log.e("FileManager", it.stackTrace.toString())
            }.addOnSuccessListener {
                fireBaseRepository.deleteUnPushedFile(fileName)
                Log.i("FileManager", "Success push $fileName!")
            }
    }

    private fun putNewData() {
        val currentState = getDataInfo(session.fileName)

        if (currentState == null) {
            createJson(
                DataFile(pointToSave, session.motion, session.date, session.fileName, session.userName),
                filePath
            )
            Log.i("FileManager", "Current state was empty!")
        } else {
            currentState.graphList.addAll(pointToSave)
            createJson(currentState, filePath)
            Log.i("FileManager", "Successful state update.")
        }

    }

    private fun createJson(dataFile: DataFile, fileName: String) {
        val jsonString = Klaxon().toJsonString(dataFile)
        val file = File(fileName)
        if (!file.exists())
            writerData(createFile(dataFile.fileName), jsonString)
        else
            writerData(file, jsonString)
    }


    private fun writerData(file: File, jsonString: String) {
        val output: Writer
        output = BufferedWriter(FileWriter(file))
        output.write(jsonString)
        output.close()
        Log.i("FileManager", "Json written to file. $jsonString")
    }

    private fun createFile(fileName: String): File {
        val storageDir = extPath
        if (storageDir != null && !storageDir.exists())
            storageDir.mkdir()
        return File.createTempFile(fileName, ".json", storageDir)
    }

    fun getDataInfo(fileName: String): DataFile? {
        val file = File("$extPath/$fileName")

        if (file.exists() && file.readBytes().isNotEmpty()) {
            return Klaxon().parse<DataFile>(file.readText())
        }
        return if (!::session.isInitialized)
            DataFile(mutableListOf(), Motion("NaN", "ac_horse"), "NaN", "NaN", "NaN")
        else
            DataFile(mutableListOf(), session.motion, session.date, session.fileName, session.userName)
    }

    fun getFileNames(): MutableList<String> {
        val listNames = mutableListOf<String>()
        extPath?.listFiles()
            ?.forEach { listNames.add(it.name) }
        return listNames
    }

    fun shareData(fileName: String?) {
        if (fileName == null) return

        val file = File("$extPath/$fileName")
        val uri: Uri = FileProvider.getUriForFile(ctx, "com.example.activity_tracker", file)
        val sendIntent = Intent()

        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my measure :0")
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri)
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        sendIntent.type = "text/*"
        val intent = Intent.createChooser(sendIntent, "Share")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        ctx.startActivity(intent)
    }
}