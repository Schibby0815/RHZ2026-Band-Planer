package com.rhz2026bandplaner.logic

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.rhz2026bandplaner.NotificationReceiver
import com.rhz2026bandplaner.data.EventType
import com.rhz2026bandplaner.data.FavoriteTimelineItem
import com.rhz2026bandplaner.data.FestivalBand
import java.time.LocalDateTime
import java.time.ZoneId

fun buildFavoriteTimeline(bandsInDay: List<FestivalBand>): List<FavoriteTimelineItem> {
    if (bandsInDay.isEmpty()) return emptyList()

    // 1. Sortieren
    val sortedBands = bandsInDay.sortedBy { band ->
        if (band.startTime.hour < 5) band.startTime.plusDays(1) else band.startTime
    }

    // 2. Cluster bilden (Alle Events, die sich zeitlich überschneiden)
    val clusters = mutableListOf<MutableList<FestivalBand>>()
    for (band in sortedBands) {
        if (clusters.isEmpty()) {
            clusters.add(mutableListOf(band))
        } else {
            val lastCluster = clusters.last()
            val clusterEnd = lastCluster.maxOf { it.endTime }
            
            // Wenn das neue Event vor dem Ende des aktuellen Clusters startet -> hinzufügen
            if (band.startTime.isBefore(clusterEnd)) {
                lastCluster.add(band)
            } else {
                clusters.add(mutableListOf(band))
            }
        }
    }

    val timeline = mutableListOf<FavoriteTimelineItem>()
    var lastEndTime: LocalDateTime? = null

    for (cluster in clusters) {
        val hasBand = cluster.any { it.type == EventType.BAND }
        val hasSigning = cluster.any { it.type == EventType.SIGNING }

        val itemToAdd: FavoriteTimelineItem = if (hasBand && hasSigning) {
            val bands = cluster.filter { it.type == EventType.BAND }
            val signings = cluster.filter { it.type == EventType.SIGNING }
            FavoriteTimelineItem.ConflictItem(bands, signings)
        } else {
            // Wenn nur Bands oder nur Signings im Cluster sind, einzeln hinzufügen
            cluster.forEach { band ->
                // Lücken-Logik innerhalb des Clusters
                addGapIfNecessary(timeline, lastEndTime, band.startTime)
                timeline.add(FavoriteTimelineItem.BandItem(band))
                lastEndTime = band.endTime
            }
            continue // Bereits hinzugefügt
        }

        // Lücken-Logik vor dem Cluster
        val currentStartTime = cluster.minOf { it.startTime }
        addGapIfNecessary(timeline, lastEndTime, currentStartTime)

        timeline.add(itemToAdd)
        lastEndTime = cluster.maxOf { it.endTime }
    }

    return timeline
}

private fun addGapIfNecessary(timeline: MutableList<FavoriteTimelineItem>, lastEnd: LocalDateTime?, currentStart: LocalDateTime) {
    if (lastEnd != null) {
        val totalMinutes = java.time.Duration.between(lastEnd, currentStart).toMinutes()
        if (totalMinutes > 5) {
            timeline.add(
                FavoriteTimelineItem.FreeTimeItem(
                    durationInMinutes = totalMinutes,
                    from = lastEnd.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")),
                    to = currentStart.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")),
                ),
            )
        }
    }
}

fun scheduleNotification(context: Context, band: FestivalBand, minutesBefore: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra("BAND_NAME", band.name)
        putExtra("STAGE", if (band.type == EventType.SIGNING) "Autogrammstunde" else band.stage)
        putExtra("MINUTES_BEFORE", minutesBefore)
        putExtra("NOTIFICATION_ID", band.id.hashCode())
        putExtra("IS_SIGNING", band.type == EventType.SIGNING)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        band.id.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    val triggerTime = band.startTime
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli() - (minutesBefore * 60 * 1000)

    if (triggerTime > System.currentTimeMillis()) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    val info = AlarmManager.AlarmClockInfo(triggerTime, pendingIntent)
                    alarmManager.setAlarmClock(info, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                }
            } else {
                val info = AlarmManager.AlarmClockInfo(triggerTime, pendingIntent)
                alarmManager.setAlarmClock(info, pendingIntent)
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
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
    if (pendingIntent != null) {
        alarmManager.cancel(pendingIntent)
    }
}
