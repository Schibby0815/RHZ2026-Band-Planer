package com.rhz2026bandplaner.ui

import android.content.SharedPreferences
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import com.rhz2026bandplaner.data.EventType
import com.rhz2026bandplaner.data.FavoriteTimelineItem
import com.rhz2026bandplaner.data.FestivalBand
import com.rhz2026bandplaner.logic.buildFavoriteTimeline
import java.util.*
import java.time.Duration
import java.time.LocalDateTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RunningOrderScreen(
    bands: List<FestivalBand>,
    sharedPreferences: SharedPreferences,
    onToggleFavorite: (String) -> Unit,
) {
    val groupedBands = remember(bands) {
        bands.groupBy { band ->
            if (band.startTime.hour < 5) band.startTime.toLocalDate().minusDays(1) else band.startTime.toLocalDate()
        }
            .mapValues { (_, bandsInDay) ->
                bandsInDay.sortedBy { band ->
                    if (band.startTime.hour < 5) band.startTime.plusDays(1) else band.startTime
                }
            }
            .toSortedMap()
    }

    var collapsedDaysStr by remember {
        mutableStateOf(sharedPreferences.getString("collapsed_days", "") ?: "")
    }
    val collapsedDaysList = collapsedDaysStr.split(",")
        .asSequence()
        .filter { it.isNotEmpty() }
        .toSet()

    // State für den Detail-Dialog
    var selectedBandForDetail by remember { mutableStateOf<FestivalBand?>(null) }

    if (selectedBandForDetail != null) {
        BandDetailDialog(band = selectedBandForDetail!!) {
            selectedBandForDetail = null
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        groupedBands.forEach { (localDate, bandsInDay) ->
            val dateKey = localDate.toString()
            val isCollapsed = dateKey in collapsedDaysList

            stickyHeader {
                val headerTextColor = MaterialTheme.colorScheme.onSecondaryContainer
                val headerBgColor = MaterialTheme.colorScheme.secondaryContainer
                
                val favoriteBandsCount = bandsInDay.count { it.isFavorite && it.type == EventType.BAND }
                val favoriteSigningsCount = bandsInDay.count { it.isFavorite && it.type == EventType.SIGNING }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(headerBgColor)
                        .clickable {
                            val newSet = if (isCollapsed) {
                                collapsedDaysList - dateKey
                            } else {
                                collapsedDaysList + dateKey
                            }
                            collapsedDaysStr = newSet.joinToString(",")
                            sharedPreferences.edit { putString("collapsed_days", collapsedDaysStr) }
                        }
                        .padding(12.dp),
                ) {
                    val formatter = java.time.format.DateTimeFormatter.ofPattern("EEEE, dd. MMMM", Locale.GERMAN)
                    val displayDate = localDate.format(formatter)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = displayDate, fontWeight = FontWeight.Bold, color = headerTextColor)
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (favoriteBandsCount > 0) {
                                Text(
                                    text = "🎸 $favoriteBandsCount",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = headerTextColor,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                            if (favoriteSigningsCount > 0) {
                                Text(
                                    text = "✍️ $favoriteSigningsCount",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = headerTextColor,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                            Text(text = if (isCollapsed) "▶" else "▼", color = headerTextColor, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (!isCollapsed) {
                items(bandsInDay) { band ->
                    BandRow(
                        band = band, 
                        onToggleFavorite = onToggleFavorite,
                        onCardClick = { selectedBandForDetail = band }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavoritesScreen(
    bands: List<FestivalBand>,
    sharedPreferences: SharedPreferences,
) {
    val favorites = bands.filter { it.isFavorite }

    // State für den Detail-Dialog
    var selectedBandForDetail by remember { mutableStateOf<FestivalBand?>(null) }

    if (selectedBandForDetail != null) {
        BandDetailDialog(band = selectedBandForDetail!!) {
            selectedBandForDetail = null
        }
    }

    if (favorites.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Noch keine Favoriten markiert.\nTippe auf das Herz bei deinen Bands!",
                color = Color.Gray,
                fontSize = 16.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    } else {
        val sortedDays = remember(favorites) {
            favorites.groupBy { band ->
                if (band.startTime.hour < 5) band.startTime.toLocalDate().minusDays(1) else band.startTime.toLocalDate()
            }.keys.sorted()
        }

        var collapsedFavoritesStr by remember {
            mutableStateOf(sharedPreferences.getString("collapsed_favorites", "") ?: "")
        }
        val collapsedFavoritesList = collapsedFavoritesStr.split(",")
            .asSequence()
            .filter { it.isNotEmpty() }
            .toSet()

        val isLightTheme = MaterialTheme.colorScheme.background.luminance() > 0.5f
        val pauseBackground = if (isLightTheme) Color(0xFF1B5E20) else Color(0xFF141F14)
        val pauseText = Color.White

        LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            sortedDays.forEach { localDate ->
                val dateKey = localDate.toString()
                val isCollapsed = dateKey in collapsedFavoritesList
                
                val bandsInDay = favorites.filter { band ->
                    val festivalDay = if (band.startTime.hour < 5) band.startTime.toLocalDate().minusDays(1) else band.startTime.toLocalDate()
                    festivalDay == localDate
                }

                stickyHeader {
                    val headerTextColor = MaterialTheme.colorScheme.onSecondaryContainer
                    val headerBgColor = MaterialTheme.colorScheme.secondaryContainer
                    
                    val favoriteBandsCount = bandsInDay.count { it.type == EventType.BAND }
                    val favoriteSigningsCount = bandsInDay.count { it.type == EventType.SIGNING }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(headerBgColor)
                            .clickable {
                                val currentSet = collapsedFavoritesList
                                val newSet = if (isCollapsed) {
                                    currentSet - dateKey
                                } else {
                                    currentSet + dateKey
                                }
                                collapsedFavoritesStr = newSet.joinToString(",")
                                sharedPreferences.edit { putString("collapsed_favorites", collapsedFavoritesStr) }
                            }
                            .padding(12.dp),
                    ) {
                        val formatter = java.time.format.DateTimeFormatter.ofPattern("EEEE, dd. MMMM", Locale.GERMAN)
                        val displayDate = localDate.format(formatter)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(text = displayDate, fontWeight = FontWeight.Bold, color = headerTextColor)
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (favoriteBandsCount > 0) {
                                    Text(
                                        text = "🎸 $favoriteBandsCount",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = headerTextColor,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                }
                                if (favoriteSigningsCount > 0) {
                                    Text(
                                        text = "✍️ $favoriteSigningsCount",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = headerTextColor,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                }
                                Text(text = if (isCollapsed) "▶" else "▼", color = headerTextColor, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                if (!isCollapsed) {
                    val timelineForDay = buildFavoriteTimeline(bandsInDay)

                    items(timelineForDay) { item ->
                        when (item) {
                            is FavoriteTimelineItem.BandItem -> {
                                CompactBandCard(
                                    band = item.band, 
                                    modifier = Modifier.fillMaxWidth(), 
                                    isConflict = false,
                                    onClick = { selectedBandForDetail = item.band }
                                )
                            }
                            is FavoriteTimelineItem.ConflictItem -> {
                                val blockStart = item.bands.minOf { it.startTime }.let { bStart ->
                                    val sStart = item.signings.minOf { it.startTime }
                                    if (bStart.isBefore(sStart)) bStart else sStart
                                }
                                val blockEnd = item.bands.maxOf { it.endTime }.let { bEnd ->
                                    val sEnd = item.signings.maxOf { it.endTime }
                                    if (bEnd.isAfter(sEnd)) bEnd else sEnd
                                }
                                
                                val totalDuration = Duration.between(blockStart, blockEnd).toMinutes()
                                val blockHeight = (totalDuration * 3).toInt().dp

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(blockHeight)
                                        .padding(vertical = 4.dp)
                                ) {
                                    TimelineColumn(
                                        items = item.bands, 
                                        blockStart = blockStart, 
                                        modifier = Modifier.weight(1f).fillMaxHeight(),
                                        onBandClick = { selectedBandForDetail = it }
                                    )
                                    TimelineColumn(
                                        items = item.signings, 
                                        blockStart = blockStart, 
                                        modifier = Modifier.weight(1f).fillMaxHeight(),
                                        onBandClick = { selectedBandForDetail = it }
                                    )
                                }
                            }
                            is FavoriteTimelineItem.FreeTimeItem -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp, horizontal = 16.dp)
                                        .background(pauseBackground, shape = MaterialTheme.shapes.small)
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    val hours = item.durationInMinutes / 60
                                    val minutes = item.durationInMinutes % 60
                                    val displayTime = if (hours > 0) {
                                        if (minutes > 0) "$hours Std. $minutes Min" else "$hours Std."
                                    } else {
                                        "$minutes Min"
                                    }

                                    Text(text = "⏱️ Pause: $displayTime (${item.from} - ${item.to})", fontSize = 13.sp, color = pauseText, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineColumn(
    items: List<FestivalBand>,
    blockStart: LocalDateTime,
    modifier: Modifier,
    onBandClick: (FestivalBand) -> Unit
) {
    Column(modifier = modifier) {
        var currentTime = blockStart
        
        items.forEach { band ->
            val gapMinutes = Duration.between(currentTime, band.startTime).toMinutes()
            if (gapMinutes > 0) {
                Spacer(modifier = Modifier.height((gapMinutes * 3).toInt().dp))
            }
            
            val durationMinutes = Duration.between(band.startTime, band.endTime).toMinutes().coerceAtLeast(1L)
            CompactBandCard(
                band = band, 
                modifier = Modifier.height((durationMinutes * 3).toInt().dp).fillMaxWidth(),
                isConflict = true,
                onClick = { onBandClick(band) }
            )
            
            currentTime = band.endTime
        }
    }
}
