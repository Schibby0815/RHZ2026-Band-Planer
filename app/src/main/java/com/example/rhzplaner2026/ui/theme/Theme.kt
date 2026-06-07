package com.example.rhzplaner2026.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// DUNKELMODUS: Schwarz / Dunkelgrün / Neon-Grün / Weißer Text
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4CAF50),           // Signalgrün für Bühnen, Highlights & Herz-Icons
    primaryContainer = Color(0xFF141F14),  // Entspricht GreenDarkCard
    onPrimaryContainer = Color.White,      // Weißer Text auf Containern
    background = Color(0xFF0A0F0A),        // App-Hintergrund (Fast Schwarz mit Grünstich)
    surface = Color(0xFF141F14),           // Bandkarten (Deep Forest)
    secondaryContainer = Color(0xFF1C2D1C),// Einklapp-Balken der Tage (Moosgrün)
    onSecondaryContainer = Color(0xFF4CAF50)// Text und Pfeile auf dem Einklapp-Balken (Leuchtgrün)
)

// HELLMODUS: Hellgrün / Weiß / Dunkelgrün / Schwarzer Text
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1B5E20),           // Tiefes Waldgrün für Highlights & wichtige Elemente
    secondary = Color(0xFF1B5E20),          // Bühne im Hellmodus ebenfalls im tiefen Grün
    background = Color(0xFFF2F7F2),        // App-Hintergrund (Soft Salbeigrün)
    surface = Color(0xFFFFFFFF),           // Bandkarten (Reinweiß für maximalen Kontrast)
    secondaryContainer = Color(0xFFD0E1D0),// Einklapp-Balken der Tage (Dezentes Hellgrün)
    onSecondaryContainer = Color(0xFF1B5E20)// Text und Pfeile auf dem Einklapp-Balken (Dunkelgrün)
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