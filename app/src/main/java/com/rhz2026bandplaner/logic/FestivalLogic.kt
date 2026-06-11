package com.rhz2026bandplaner.logic

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.rhz2026bandplaner.MainActivity
import com.rhz2026bandplaner.NotificationReceiver
import com.rhz2026bandplaner.data.FavoriteTimelineItem
import com.rhz2026bandplaner.data.FestivalBand
import java.time.ZoneId

fun buildFavoriteTimeline(bandsInDay: List<FestivalBand>): List<FavoriteTimelineItem> {
    if (bandsInDay.isEmpty()) return emptyList()

    val sortedBands = bandsInDay.sortedBy { band ->
        if (band.startTime.hour < 5) {
            band.startTime.plusDays(1)
        } else {
            band.startTime
        }
    }

    val timeline = mutableListOf<FavoriteTimelineItem>()
    timeline.add(FavoriteTimelineItem.BandItem(sortedBands[0]))

    for (i in 1 until sortedBands.size) {
        val previousBand = sortedBands[i - 1]
        val currentBand = sortedBands[i]

        val previousEnd = previousBand.endTime
        val currentStart = currentBand.startTime

        val totalMinutes = java.time.Duration.between(previousEnd, currentStart).toMinutes()

        if (totalMinutes > 5) {
            timeline.add(
                FavoriteTimelineItem.FreeTimeItem(
                    durationInMinutes = totalMinutes,
                    from = previousBand.formattedTime.substringAfter("- ").trim(),
                    to = currentBand.formattedTime.substringBefore(" -").trim()
                )
            )
        }
        timeline.add(FavoriteTimelineItem.BandItem(currentBand))
    }
    return timeline
}

fun scheduleNotification(context: Context, band: FestivalBand, minutesBefore: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra("BAND_NAME", band.name)
        putExtra("STAGE", band.stage)
        putExtra("MINUTES_BEFORE", minutesBefore)
        putExtra("NOTIFICATION_ID", band.id.hashCode())
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        band.id.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val triggerTime = band.startTime
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli() - (minutesBefore * 60 * 1000)

    if (triggerTime > System.currentTimeMillis()) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}

fun cancelNotification(context: Context, band: FestivalBand) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, NotificationReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        band.id.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    if (pendingIntent != null) {
        alarmManager.cancel(pendingIntent)
    }
}
