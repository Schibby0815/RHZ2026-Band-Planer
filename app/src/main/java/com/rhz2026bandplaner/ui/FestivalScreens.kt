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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.core.content.edit
import com.rhz2026bandplaner.data.FavoriteTimelineItem
import com.rhz2026bandplaner.data.FestivalBand
import com.rhz2026bandplaner.logic.buildFavoriteTimeline
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RunningOrderScreen(
    bands: List<FestivalBand>,
    sharedPreferences: SharedPreferences,
    onToggleFavorite: (String) -> Unit,
) {
    val groupedBands = remember(bands) {
        bands.groupBy { band ->
            // Festival-Tag Logik: Bands vor 5 Uhr morgens gehören zum Vortag
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

    LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        groupedBands.forEach { (localDate, bandsInDay) ->
            val dateKey = localDate.toString()
            val isCollapsed = dateKey in collapsedDaysList

            stickyHeader {
                val headerTextColor = MaterialTheme.colorScheme.onSecondaryContainer
                val headerBgColor = MaterialTheme.colorScheme.secondaryContainer

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
                        Text(text = if (isCollapsed) "▶" else "▼", color = headerTextColor, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (!isCollapsed) {
                items(bandsInDay) { band ->
                    BandRow(band = band, onToggleFavorite = onToggleFavorite)
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
                // Festival-Tag Logik: Bands vor 5 Uhr morgens gehören zum Vortag
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
        val cardBackground = if (isLightTheme) Color.White else MaterialTheme.colorScheme.surfaceVariant
        val primaryText = if (isLightTheme) Color.Black else Color.White
        val pauseBackground = if (isLightTheme) Color(0xFF1B5E20) else Color(0xFF141F14)
        val pauseText = Color.White
        val uriHandler = LocalUriHandler.current

        LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            sortedDays.forEach { localDate ->
                val dateKey = localDate.toString()
                val isCollapsed = dateKey in collapsedFavoritesList

                stickyHeader {
                    val headerTextColor = MaterialTheme.colorScheme.onSecondaryContainer
                    val headerBgColor = MaterialTheme.colorScheme.secondaryContainer

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(headerBgColor)
                            .clickable {
                                val newSet = if (isCollapsed) {
                                    collapsedFavoritesList - dateKey
                                } else {
                                    collapsedFavoritesList + dateKey
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
                            Text(text = if (isCollapsed) "▶" else "▼", color = headerTextColor, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (!isCollapsed) {
                    val bandsInDay = favorites.filter { band ->
                        val festivalDay = if (band.startTime.hour < 5) band.startTime.toLocalDate().minusDays(1) else band.startTime.toLocalDate()
                        festivalDay == localDate
                    }
                    val timelineForDay = buildFavoriteTimeline(bandsInDay)

                    items(timelineForDay) { item ->
                        when (item) {
                            is FavoriteTimelineItem.BandItem -> {
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = cardBackground),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = item.band.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = primaryText)
                                            val stageTextColor = if (isLightTheme) Color(0xFF1B5E20) else Color(0xFF81C784)
                                            Text(text = item.band.stage, fontSize = 12.sp, color = stageTextColor)
                                        }

                                        Text(
                                            text = "🎧",
                                            fontSize = 20.sp,
                                            modifier = Modifier
                                                .clickable {
                                                    val url = "https://open.spotify.com/search/" + item.band.name.replace(" ", "%20")
                                                    uriHandler.openUri(url)
                                                }
                                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                        )

                                        Text(text = item.band.formattedTime, fontWeight = FontWeight.Medium, color = primaryText)
                                    }
                                }
                            }
                            is FavoriteTimelineItem.FreeTimeItem -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp, horizontal = 16.dp)
                                        .background(pauseBackground, shape = MaterialTheme.shapes.small)
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
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
