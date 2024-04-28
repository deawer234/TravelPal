package com.example.travelpal.ui.manager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.travelpal.ui.CameraActivity

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val cameraIntent = Intent(context, CameraActivity::class.java)
        val id = intent.getLongExtra("travelEntityId", -1L)
        intent.putExtra("id", id)

        cameraIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(cameraIntent)
    }
}