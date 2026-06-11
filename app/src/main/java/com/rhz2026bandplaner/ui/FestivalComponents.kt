package com.rhz2026bandplaner.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rhz2026bandplaner.data.FestivalBand

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
