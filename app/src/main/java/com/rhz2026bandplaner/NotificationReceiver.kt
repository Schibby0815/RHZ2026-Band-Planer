package com.rhz2026bandplaner

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val bandName = intent.getStringExtra("BAND_NAME") ?: "Eine Band"
        val stage = intent.getStringExtra("STAGE") ?: "Bühne"
        val minutesBefore = intent.getIntExtra("MINUTES_BEFORE", 5)
        val notificationId = intent.getIntExtra("NOTIFICATION_ID", 0)
        val isSigning = intent.getBooleanExtra("IS_SIGNING", false)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "rockharz_alerts"

        val channel = NotificationChannel(channelId, "Festival Wecker", NotificationManager.IMPORTANCE_HIGH).apply {
            description = "Benachrichtigungen für Band-Starts"
            enableLights(true)
            enableVibration(true)
            lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
        }
        notificationManager.createNotificationChannel(channel)

        // Intent zum Öffnen der App (MainActivity)
        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val title = if (isSigning) "Autogrammstunde startet! ✍️" else "Gleich geht's los! 🎸"
        val contentText = if (isSigning) {
            "Die Autogrammstunde von $bandName startet gleich!"
        } else {
            "$bandName spielt in $minutesBefore Minuten auf der $stage!"
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}
