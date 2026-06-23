package com.rhz2026bandplaner.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// DUNKELMODUS: Schwarz / Dunkelgrün / Neon-Grün / Weißer Text
private val DarkColorScheme = darkColorScheme(
    primary = GreenNeonAccent,           // Signalgrün für Bühnen, Highlights & Herz-Icons
    primaryContainer = GreenDarkCard,  // Entspricht GreenDarkCard
    onPrimaryContainer = Color.White,      // Weißer Text auf Containern
    background = GreenDarkBg,        // App-Hintergrund (Fast Schwarz mit Grünstich)
    surface = GreenDarkCard,           // Bandkarten (Deep Forest)
    secondaryContainer = GreenDarkHeader,// Einklapp-Balken der Tage (Moosgrün)
    onSecondaryContainer = GreenNeonAccent, // Text und Pfeile auf dem Einklapp-Balken (Leuchtgrün)
)

// HELLMODUS: Hellgrün / Weiß / Dunkelgrün / Schwarzer Text
private val LightColorScheme = lightColorScheme(
    primary = GreenDeepAccent,           // Tiefes Waldgrün für Highlights & wichtige Elemente
    secondary = GreenDeepAccent,          // Bühne im Hellmodus ebenfalls im tiefen Grün
    background = GreenLightBg,        // App-Hintergrund (Soft Salbeigrün)
    surface = GreenLightCard,           // Bandkarten (Reinweiß für maximalen Kontrast)
    secondaryContainer = GreenLightHeader,// Einklapp-Balken der Tage (Dezentes Hellgrün)
    onSecondaryContainer = GreenDeepAccent// Text und Pfeile auf dem Einklapp-Balken (Dunkelgrün)
)

@Composable
fun RockharzPlanerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Nutzt standardmäßig das System, kann aber überschrieben werden
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}