package com.example.activity_tracker.application

import android.app.AlertDialog
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.example.activity_tracker.R
import com.example.activity_tracker.repositories.ApplicationRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class DialogCreateUser(private val activity: FragmentActivity) : KoinComponent {

    fun createDialog(textView: TextView) {
        val inflater = activity.layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_create, null)
        val createUser = AlertDialog.Builder(activity).setView(dialogLayout)
        val userName = dialogLayout.findViewById<EditText>(R.id.setUserName)
        val applicationRepository: ApplicationRepository by inject()

        createUser.setPositiveButton("Create")
        { _, _ ->
            applicationRepository.setCurrentUser(userName.text.toString())
            textView.text = applicationRepository.getCurrentUser()
        }
        createUser.setNegativeButton("CANCEL")
        { dialog, _ -> dialog.cancel() }

        createUser.show().window?.setBackgroundDrawableResource(R.drawable.dialog_small)
    }
}