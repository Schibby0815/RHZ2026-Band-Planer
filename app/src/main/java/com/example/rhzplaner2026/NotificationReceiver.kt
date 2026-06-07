package com.example.rhzplaner2026

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val bandName = intent.getStringExtra("BAND_NAME") ?: "Eine Band"
        val stage = intent.getStringExtra("STAGE") ?: "Bühne"
        val notificationId = intent.getIntExtra("NOTIFICATION_ID", 0)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "rockharz_alerts"

        // Ab Android 8 (Oreo) benötigt jede Benachrichtigung einen verpflichtenden Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Festival Wecker", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // Nutzt ein Standard-System-Wecker-Icon
            .setContentTitle("Gleich geht's los! 🎸")
            .setContentText("$bandName spielt in 5 Minuten auf der $stage!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}