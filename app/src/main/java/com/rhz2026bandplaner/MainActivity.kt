package com.rhz2026bandplaner

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.rhz2026bandplaner.data.rockharz2026Bands
import com.rhz2026bandplaner.logic.cancelNotification
import com.rhz2026bandplaner.logic.scheduleNotification
import com.rhz2026bandplaner.ui.FavoritesScreen
import com.rhz2026bandplaner.ui.InfoDialog
import com.rhz2026bandplaner.ui.RunningOrderScreen
import com.rhz2026bandplaner.ui.theme.RockharzPlanerTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var notificationPermissionGranted by mutableStateOf(value = false)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        notificationPermissionGranted = isGranted
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("rockharz_prefs", MODE_PRIVATE)

        notificationPermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        checkAndRequestNotificationPermission(sharedPreferences)

        setContent {
            var isDarkMode by remember {
                mutableStateOf(sharedPreferences.getBoolean("is_dark_mode", true))
            }

            RockharzPlanerTheme(darkTheme = isDarkMode) {
                RockharzApp(
                    sharedPreferences = sharedPreferences,
                    isDarkMode = isDarkMode,
                    onToggleTheme = {
                        isDarkMode = !isDarkMode
                        sharedPreferences.edit(commit = true) { putBoolean("is_dark_mode", isDarkMode) }
                    },
                    hasPermission = { notificationPermissionGranted },
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }
        }
    }

    private fun checkAndRequestNotificationPermission(sharedPreferences: SharedPreferences) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED

            val alreadyAskedBefore = sharedPreferences.getBoolean("notification_asked_once", false)

            if (!hasPermission && !alreadyAskedBefore) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                sharedPreferences.edit {
                    putBoolean("notification_asked_once", true)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RockharzApp(
    sharedPreferences: SharedPreferences,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    hasPermission: () -> Boolean,
    onRequestPermission: () -> Unit,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var reminderMinutes by remember {
        mutableIntStateOf(
            if (!hasPermission()) -1 else sharedPreferences.getInt("reminder_minutes", 5),
        )
    }
    var showTimeMenu by remember { mutableStateOf(value = false) }

    var bandsState by remember {
        mutableStateOf(
            run {
                val savedFavoriteIds = sharedPreferences.getStringSet("favorite_bands", emptySet()) ?: emptySet()
                rockharz2026Bands.map { band ->
                    if (savedFavoriteIds.contains(band.id)) band.copy(isFavorite = true) else band
                }
            },
        )
    }

    LaunchedEffect(hasPermission()) {
        reminderMinutes = if (hasPermission()) sharedPreferences.getInt("reminder_minutes", 5) else -1
    }

    val updateAllActiveAlarms: (Int) -> Unit = { newMinutes ->
        bandsState.forEach { band ->
            if (band.isFavorite) {
                cancelNotification(context, band)
                if (newMinutes != -1) scheduleNotification(context, band, newMinutes)
            }
        }
    }

    val handleTimeSelection: (Int) -> Unit = { minutes ->
        if (!hasPermission()) {
            android.widget.Toast.makeText(
                context,
                "Bitte erlaube Benachrichtigungen, damit der Wecker gestellt werden kann!",
                android.widget.Toast.LENGTH_LONG,
            ).show()
            onRequestPermission()
        } else {
            reminderMinutes = minutes
            sharedPreferences.edit(commit = true) { putInt("reminder_minutes", minutes) }
            updateAllActiveAlarms(minutes)
            showTimeMenu = false
        }
    }

    val tabs = listOf("Running Order", "Meine Favoriten")
    val pagerState = rememberPagerState { tabs.size }

    var showInfoDialog by remember {
        mutableStateOf(!sharedPreferences.getBoolean("first_start_done", false))
    }

    if (showInfoDialog) {
        InfoDialog {
            showInfoDialog = false
            sharedPreferences.edit { putBoolean("first_start_done", true) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name), fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                actions = {
                    Box {
                        IconButton(onClick = { showTimeMenu = true }) {
                            Text(text = if(reminderMinutes == -1) "🔇" else "⏱️", fontSize = 20.sp)
                        }
                        DropdownMenu(
                            expanded = showTimeMenu,
                            onDismissRequest = { showTimeMenu = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text("Wecker deaktivieren ❌ ${if(reminderMinutes == -1) "✓" else ""}") },
                                onClick = {
                                    reminderMinutes = -1
                                    sharedPreferences.edit { putInt("reminder_minutes", -1) }
                                    updateAllActiveAlarms(-1)
                                    showTimeMenu = false
                                }
                            )
                            HorizontalDivider()
                            listOf(5, 10, 15, 30).forEach { mins ->
                                DropdownMenuItem(
                                    text = { Text("Erinnerung: $mins Min vorher ${if(reminderMinutes == mins) "✓" else ""}") },
                                    onClick = { handleTimeSelection(mins) }
                                )
                            }
                        }
                    }
                    IconButton(onClick = { showInfoDialog = true }) { Text(text = "ℹ️", fontSize = 20.sp) }
                    IconButton(onClick = onToggleTheme) { Text(text = if (isDarkMode) "☀️" else "🌙", fontSize = 20.sp) }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> RunningOrderScreen(
                        bands = bandsState,
                        sharedPreferences = sharedPreferences,
                    ) { bandId ->
                        val updatedBands = bandsState.map { band ->
                            if (band.id == bandId) {
                                val newFavoriteState = !band.isFavorite
                                if (newFavoriteState) {
                                    if (reminderMinutes != -1) scheduleNotification(context, band, reminderMinutes)
                                } else {
                                    cancelNotification(context, band)
                                }
                                band.copy(isFavorite = newFavoriteState)
                            } else {
                                band
                            }
                        }
                        bandsState = updatedBands
                        val favoriteIds = updatedBands.asSequence()
                            .filter { it.isFavorite }
                            .map { it.id }
                            .toSet()

                        // 1. Zuerst SharedPreferences synchron mit commit() schreiben
                        sharedPreferences.edit(commit = true) {
                            putStringSet("favorite_bands", favoriteIds)
                        }
                    }

                    1 -> FavoritesScreen(
                        bands = bandsState,
                        sharedPreferences = sharedPreferences
                    )
                }
            }
        }
    }
}
