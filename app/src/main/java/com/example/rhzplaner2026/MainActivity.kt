package com.rhz2026bandplaner

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import java.time.ZoneId
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import com.example.rhzplaner2026.ui.theme.RockharzPlanerTheme
import androidx.compose.runtime.saveable.rememberSaveable

// WICHTIG: Diese Imports garantieren, dass 'by rememberSaveable' fehlerfrei funktioniert
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

// ==========================================
// 1. DATENMODELLE & LOGIK
// ==========================================

data class FestivalBand(
    val id: String,
    val name: String,
    val stage: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val isFavorite: Boolean = false
) {
    val formattedTime: String
        get() = "${startTime.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${endTime.format(DateTimeFormatter.ofPattern("HH:mm"))}"
}

sealed class FavoriteTimelineItem {
    data class BandItem(val band: FestivalBand) : FavoriteTimelineItem()
    data class FreeTimeItem(val durationInMinutes: Long, val from: String, val to: String) : FavoriteTimelineItem()
}

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

// ==========================================
// 1b. BENACHRICHTIGUNGS-HELFER
// ==========================================

fun scheduleNotification(context: Context, band: FestivalBand) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra("BAND_NAME", band.name)
        putExtra("STAGE", band.stage)
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
        .toEpochMilli() - (5 * 60 * 1000)

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

// ==========================================
// 2. DIE OFFIZIELLE BANDLISTE (ROCKHARZ 2026)
// ==========================================

val rockharz2026Bands = listOf(
    FestivalBand("1", "Heavysaurus", "Dark Stage", LocalDateTime.of(2026, 7, 1, 15, 30), LocalDateTime.of(2026, 7, 1, 16, 15)),
    FestivalBand("2", "Soulbound", "Rock Stage", LocalDateTime.of(2026, 7, 1, 16, 20), LocalDateTime.of(2026, 7, 1, 17, 5)),
    FestivalBand("3", "Harakiri For The Sky", "Dark Stage", LocalDateTime.of(2026, 7, 1, 17, 10), LocalDateTime.of(2026, 7, 1, 17, 55)),
    FestivalBand("4", "The Haunted", "Rock Stage", LocalDateTime.of(2026, 7, 1, 18, 0), LocalDateTime.of(2026, 7, 1, 18, 45)),
    FestivalBand("5", "Ensiferum", "Dark Stage", LocalDateTime.of(2026, 7, 1, 18, 50), LocalDateTime.of(2026, 7, 1, 19, 40)),
    FestivalBand("6", "Paradise Lost", "Rock Stage", LocalDateTime.of(2026, 7, 1, 19, 45), LocalDateTime.of(2026, 7, 1, 20, 45)),
    FestivalBand("7", "Black Label Society", "Dark Stage", LocalDateTime.of(2026, 7, 1, 20, 50), LocalDateTime.of(2026, 7, 1, 21, 55)),
    FestivalBand("8", "Helloween", "Rock Stage", LocalDateTime.of(2026, 7, 1, 22, 0), LocalDateTime.of(2026, 7, 2, 0, 0)),
    FestivalBand("9", "Steve 'N' Seagulls", "Dark Stage", LocalDateTime.of(2026, 7, 1, 0, 10), LocalDateTime.of(2026, 7, 2, 1, 10)),

    FestivalBand("10", "Final Cry", "Dark Stage", LocalDateTime.of(2026, 7, 2, 11, 50), LocalDateTime.of(2026, 7, 2, 12, 20)),
    FestivalBand("11", "Die Habenichtse", "Rock Stage", LocalDateTime.of(2026, 7, 2, 12, 25), LocalDateTime.of(2026, 7, 2, 13, 0)),
    FestivalBand("12", "Mittel Alta", "Dark Stage", LocalDateTime.of(2026, 7, 2, 13, 5), LocalDateTime.of(2026, 7, 2, 13, 45)),
    FestivalBand("13", "Hagane", "Rock Stage", LocalDateTime.of(2026, 7, 2, 13, 50), LocalDateTime.of(2026, 7, 2, 14, 25)),
    FestivalBand("14", "Stahlmann", "Dark Stage", LocalDateTime.of(2026, 7, 2, 14, 30), LocalDateTime.of(2026, 7, 2, 15, 10)),
    FestivalBand("15", "Sagenbringer", "Rock Stage", LocalDateTime.of(2026, 7, 2, 15, 15), LocalDateTime.of(2026, 7, 2, 16, 0)),
    FestivalBand("16", "Dogma", "Dark Stage", LocalDateTime.of(2026, 7, 2, 16, 0), LocalDateTime.of(2026, 7, 2, 16, 50)),
    FestivalBand("17", "Warmen", "Rock Stage", LocalDateTime.of(2026, 7, 2, 16, 55), LocalDateTime.of(2026, 7, 2, 17, 45)),
    FestivalBand("18", "Decapitated", "Dark Stage", LocalDateTime.of(2026, 7, 2, 17, 50), LocalDateTime.of(2026, 7, 2, 18, 40)),
    FestivalBand("19", "Betontod", "Rock Stage", LocalDateTime.of(2026, 7, 2, 18, 45), LocalDateTime.of(2026, 7, 2, 19, 35)),
    FestivalBand("20", "Agnostic Front", "Dark Stage", LocalDateTime.of(2026, 7, 2, 19, 40), LocalDateTime.of(2026, 7, 2, 20, 30)),
    FestivalBand("21", "Hämatom", "Rock Stage", LocalDateTime.of(2026, 7, 2, 20, 35), LocalDateTime.of(2026, 7, 2, 21, 35)),
    FestivalBand("22", "Avatar", "Dark Stage", LocalDateTime.of(2026, 7, 2, 21, 40), LocalDateTime.of(2026, 7, 2, 22, 40)),
    FestivalBand("23", "Alice Cooper", "Rock Stage", LocalDateTime.of(2026, 7, 2, 22, 45), LocalDateTime.of(2026, 7, 3, 0, 0)),
    FestivalBand("24", "Dominum", "Dark Stage", LocalDateTime.of(2026, 7, 2, 0, 5), LocalDateTime.of(2026, 7, 3, 1, 0)),

    FestivalBand("25", "Rodeo 5000", "Rock Stage", LocalDateTime.of(2026, 7, 3, 11, 20), LocalDateTime.of(2026, 7, 3, 11, 50)),
    FestivalBand("26", "Haggefugg", "Dark Stage", LocalDateTime.of(2026, 7, 3, 11, 55), LocalDateTime.of(2026, 7, 3, 12, 25)),
    FestivalBand("27", "Motorjesus", "Rock Stage", LocalDateTime.of(2026, 7, 3, 12, 30), LocalDateTime.of(2026, 7, 3, 13, 0)),
    FestivalBand("28", "Cypecore", "Dark Stage", LocalDateTime.of(2026, 7, 3, 13, 5), LocalDateTime.of(2026, 7, 3, 13, 40)),
    FestivalBand("29", "Hiraes", "Rock Stage", LocalDateTime.of(2026, 7, 3, 13, 45), LocalDateTime.of(2026, 7, 3, 14, 30)),
    FestivalBand("30", "Gothminister", "Dark Stage", LocalDateTime.of(2026, 7, 3, 14, 35), LocalDateTime.of(2026, 7, 3, 15, 20)),
    FestivalBand("31", "Rauhbein", "Rock Stage", LocalDateTime.of(2026, 7, 3, 15, 25), LocalDateTime.of(2026, 7, 3, 16, 10)),
    FestivalBand("32", "Walls Of Jericho", "Dark Stage", LocalDateTime.of(2026, 7, 3, 16, 15), LocalDateTime.of(2026, 7, 3, 17, 0)),
    FestivalBand("33", "Fiddler's Green", "Rock Stage", LocalDateTime.of(2026, 7, 3, 17, 5), LocalDateTime.of(2026, 7, 3, 17, 50)),
    FestivalBand("34", "Die Apokalyptischen Reiter", "Dark Stage", LocalDateTime.of(2026, 7, 3, 17, 55), LocalDateTime.of(2026, 7, 3, 18, 40)),
    FestivalBand("35", "Biohazard", "Rock Stage", LocalDateTime.of(2026, 7, 3, 18, 45), LocalDateTime.of(2026, 7, 3, 19, 30)),
    FestivalBand("36", "P.O.D.", "Dark Stage", LocalDateTime.of(2026, 7, 3, 19, 35), LocalDateTime.of(2026, 7, 3, 20, 35)),
    FestivalBand("37", "Subway To Sally", "Rock Stage", LocalDateTime.of(2026, 7, 3, 20, 40), LocalDateTime.of(2026, 7, 3, 21, 40)),
    FestivalBand("38", "Airbourne", "Dark Stage", LocalDateTime.of(2026, 7, 3, 21, 45), LocalDateTime.of(2026, 7, 3, 22, 45)),
    FestivalBand("39", "Kreator", "Rock Stage", LocalDateTime.of(2026, 7, 3, 22, 50), LocalDateTime.of(2026, 7, 4, 0, 20)),

    FestivalBand("40", "Pinhead", "Rock Stage", LocalDateTime.of(2026, 7, 4, 11, 20), LocalDateTime.of(2026, 7, 4, 11, 50)),
    FestivalBand("41", "Drone", "Dark Stage", LocalDateTime.of(2026, 7, 4, 11, 55), LocalDateTime.of(2026, 7, 4, 12, 25)),
    FestivalBand("42", "Tailgunner", "Rock Stage", LocalDateTime.of(2026, 7, 4, 12, 30), LocalDateTime.of(2026, 7, 4, 13, 10)),
    FestivalBand("43", "Necrotted", "Dark Stage", LocalDateTime.of(2026, 7, 4, 13, 15), LocalDateTime.of(2026, 7, 4, 13, 55)),
    FestivalBand("44", "Tungsten", "Rock Stage", LocalDateTime.of(2026, 7, 4, 14, 0), LocalDateTime.of(2026, 7, 4, 14, 40)),
    FestivalBand("45", "Crypta", "Dark Stage", LocalDateTime.of(2026, 7, 4, 14, 45), LocalDateTime.of(2026, 7, 4, 15, 25)),
    FestivalBand("46", "Artillery", "Rock Stage", LocalDateTime.of(2026, 7, 4, 15, 30), LocalDateTime.of(2026, 7, 4, 16, 10)),
    FestivalBand("47", "Majestica", "Dark Stage", LocalDateTime.of(2026, 7, 4, 16, 15), LocalDateTime.of(2026, 7, 4, 17, 0)),
    FestivalBand("48", "Annisokay", "Rock Stage", LocalDateTime.of(2026, 7, 4, 17, 5), LocalDateTime.of(2026, 7, 4, 17, 50)),
    FestivalBand("49", "Finntroll", "Dark Stage", LocalDateTime.of(2026, 7, 4, 17, 55), LocalDateTime.of(2026, 7, 4, 18, 40)),
    FestivalBand("50", "Danko Jones", "Rock Stage", LocalDateTime.of(2026, 7, 4, 18, 45), LocalDateTime.of(2026, 7, 4, 19, 30)),
    FestivalBand("51", "Doro", "Dark Stage", LocalDateTime.of(2026, 7, 4, 19, 35), LocalDateTime.of(2026, 7, 4, 20, 35)),
    FestivalBand("52", "Knorkator", "Rock Stage", LocalDateTime.of(2026, 7, 4, 20, 40), LocalDateTime.of(2026, 7, 4, 21, 40)),
    FestivalBand("53", "Emperor", "Dark Stage", LocalDateTime.of(2026, 7, 4, 21, 45), LocalDateTime.of(2026, 7, 4, 22, 45)),
    FestivalBand("54", "Feuerschwanz", "Rock Stage", LocalDateTime.of(2026, 7, 4, 22, 45), LocalDateTime.of(2026, 7, 5, 0, 15)),
    FestivalBand("55", "Soen", "Dark Stage", LocalDateTime.of(2026, 7, 4, 0, 30), LocalDateTime.of(2026, 7, 5, 1, 30))
)

// ==========================================
// 3. ANDROID ACTIVITY & UI-OBERFLÄCHE
// ==========================================

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("rockharz_prefs", MODE_PRIVATE)

        checkAndRequestNotificationPermission()

        setContent {
            var isDarkMode by remember { mutableStateOf(true) }

            RockharzPlanerTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RockharzApp(
                        sharedPreferences = sharedPreferences,
                        isDarkMode = isDarkMode,
                        onToggleTheme = { isDarkMode = !isDarkMode }
                    )
                }
            }
        }
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RockharzApp(
    sharedPreferences: SharedPreferences,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    var bandsState by remember {
        mutableStateOf(
            run {
                val savedFavoriteIds = sharedPreferences.getStringSet("favorite_bands", emptySet()) ?: emptySet()
                rockharz2026Bands.map { band ->
                    if (savedFavoriteIds.contains(band.id)) {
                        band.copy(isFavorite = true)
                    } else {
                        band
                    }
                }
            }
        )
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Running Order", "Meine Favoriten")

    // NEU: Status für die Anzeige des Info-Bildschirms (Dialog)
    // Liest aus, ob es das erste Mal geöffnet wird. Wenn der Key "first_start_done" fehlt, ist es true.
    var showInfoDialog by remember {
        mutableStateOf(!sharedPreferences.getBoolean("first_start_done", false))
    }

    // Wenn der Info-Dialog aktiv ist, zeigen wir ihn hier an
    if (showInfoDialog) {
        InfoDialog(
            onDismiss = {
                showInfoDialog = false
                // Speichert dauerhaft ab, dass der erste Start erledigt ist
                sharedPreferences.edit { putBoolean("first_start_done", true) }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name), fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                actions = {
                    // NEU: Info-Button direkt neben dem Theme-Schalter
                    IconButton(onClick = { showInfoDialog = true }) {
                        Text(text = "ℹ️", fontSize = 20.sp)
                    }
                    IconButton(onClick = onToggleTheme) {
                        Text(text = if (isDarkMode) "☀️" else "🌙", fontSize = 20.sp)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            PrimaryTabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }

            when (selectedTab) {
                0 -> RunningOrderScreen(
                    bands = bandsState,
                    sharedPreferences = sharedPreferences,
                    onToggleFavorite = { bandId ->
                        val updatedBands = bandsState.map { band ->
                            if (band.id == bandId) {
                                val newFavoriteState = !band.isFavorite
                                if (newFavoriteState) {
                                    scheduleNotification(context, band)
                                } else {
                                    cancelNotification(context, band)
                                }
                                band.copy(isFavorite = newFavoriteState)
                            } else {
                                band
                            }
                        }
                        bandsState = updatedBands
                        val favoriteIdsToSave = updatedBands.filter { it.isFavorite }.map { it.id }.toSet()
                        sharedPreferences.edit { putStringSet("favorite_bands", favoriteIdsToSave) }
                    }
                )
                1 -> FavoritesScreen(
                    bands = bandsState,
                    sharedPreferences = sharedPreferences
                )
            }
        }
    }
}

// ==========================================
// NEU: DER INFO-BILDSCHIRM (DIALOG)
// ==========================================
@Composable
fun InfoDialog(onDismiss: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    val paypalUrl = "https://paypal.me/andreasvolkhausen"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Willkommen!", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Danke das du die App Benutzt! Sie ist ein reines Hobby Projekt, kostenfrei und ohne Werbung. Die App steht in keiner Verbindung mit dem Rockharz oder den Veranstaltern. Falls Dir die App gefällt spendier mir gerne ein Bier.",
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
                // ÄNDERUNG HIER: Schöner Text anstatt der nackten URL, Ziel bleibt identisch
                Text(
                    text = "🍻 Bier spendieren (PayPal)🍻",
                    color = Color(0xFF2196F3),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { uriHandler.openUri(paypalUrl) }
                        .padding(vertical = 4.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Schließen")
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RunningOrderScreen(
    bands: List<FestivalBand>,
    sharedPreferences: SharedPreferences,
    onToggleFavorite: (String) -> Unit
) {
    val groupedBands = remember(bands) {
        bands.groupBy { it.startTime.toLocalDate() }
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
    val collapsedDaysList = collapsedDaysStr.split(",").filter { it.isNotEmpty() }.toSet()

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
                        .padding(12.dp)
                ) {
                    val displayDate = when (localDate.dayOfMonth) {
                        1 -> "Mittwoch, 01. Juli"
                        2 -> "Donnerstag, 02. Juli"
                        3 -> "Freitag, 03. Juli"
                        4 -> "Samstag, 04. Juli"
                        else -> "${localDate.dayOfMonth}. Juli"
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
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
    sharedPreferences: SharedPreferences
) {
    val favorites = bands.filter { it.isFavorite }

    if (favorites.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Noch keine Favoriten markiert.\nTippe auf das Herz bei deinen Bands!",
                color = Color.Gray,
                fontSize = 16.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    } else {
        val sortedDays = remember(favorites) {
            favorites.groupBy { it.startTime.toLocalDate() }.keys.sorted()
        }

        var collapsedFavoritesStr by remember {
            mutableStateOf(sharedPreferences.getString("collapsed_favorites", "") ?: "")
        }
        val collapsedFavoritesList = collapsedFavoritesStr.split(",").filter { it.isNotEmpty() }.toSet()

        val isLightTheme = MaterialTheme.colorScheme.background.luminance() > 0.5f
        val cardBackground = if (isLightTheme) Color.White else MaterialTheme.colorScheme.surfaceVariant
        val primaryText = if (isLightTheme) Color.Black else Color.White
        val pauseBackground = if (isLightTheme) Color(0xFF1B5E20) else Color(0xFF141F14)
        val pauseText = Color.White

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
                            .padding(12.dp)
                    ) {
                        val displayDate = when (localDate.dayOfMonth) {
                            1 -> "Mittwoch, 01. Juli"
                            2 -> "Donnerstag, 02. Juli"
                            3 -> "Freitag, 03. Juli"
                            4 -> "Samstag, 04. Juli"
                            else -> "${localDate.dayOfMonth}. Juli"
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = displayDate, fontWeight = FontWeight.Bold, color = headerTextColor)
                            Text(text = if (isCollapsed) "▶" else "▼", color = headerTextColor, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (!isCollapsed) {
                    val bandsInDay = favorites.filter { it.startTime.toLocalDate() == localDate }
                    val timelineForDay = buildFavoriteTimeline(bandsInDay)

                    items(timelineForDay) { item ->
                        when (item) {
                            is FavoriteTimelineItem.BandItem -> {
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = cardBackground),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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

@Composable
fun BandRow(
    band: FestivalBand,
    onToggleFavorite: (String) -> Unit
) {
    val isLightTheme = MaterialTheme.colorScheme.background.luminance() > 0.5f
    val stageTextColor = if (isLightTheme) Color(0xFF1B5E20) else Color(0xFF81C784)
    val textColor = if (isLightTheme) Color.Black else Color.White

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = if (isLightTheme) Color.White else MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = band.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (isLightTheme) Color.Black else Color.White)
                Text(text = band.stage, fontSize = 12.sp, color = stageTextColor)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = band.formattedTime, fontWeight = FontWeight.Medium, color = textColor, modifier = Modifier.padding(end = 8.dp))
                IconButton(onClick = { onToggleFavorite(band.id) }) {
                    Icon(
                        imageVector = if (band.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorit",
                        tint = if (band.isFavorite) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }
        }
    }
}