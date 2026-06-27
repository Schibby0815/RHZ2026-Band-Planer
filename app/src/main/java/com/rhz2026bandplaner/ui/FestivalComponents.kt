package com.rhz2026bandplaner.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
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
import com.rhz2026bandplaner.data.EventType
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
                    lineHeight = 22.sp,
                )
                Text(
                    text = "🍻 Bier spendieren (PayPal)🍻",
                    color = Color(0xFF2196F3),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { uriHandler.openUri(paypalUrl) }
                        .padding(vertical = 4.dp),
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
fun BandDetailDialog(band: FestivalBand, onDismiss: () -> Unit) {
    val isLightTheme = MaterialTheme.colorScheme.background.luminance() > 0.5f
    val stageTextColor = if (isLightTheme) Color(0xFF1B5E20) else Color(0xFF81C784)
    val textColor = if (isLightTheme) Color.Black else Color.White
    val isSigning = band.type == EventType.SIGNING

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = band.name.removeSuffix(" (Signing)"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = textColor
                )
                if (band.type == EventType.BAND && (band.countryEmoji.isNotEmpty() || band.genre.isNotEmpty() || band.foundedYear.isNotEmpty())) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        if (band.countryEmoji.isNotEmpty()) {
                            Text(
                                text = "${band.countryEmoji} ${band.countryName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (band.genre.isNotEmpty()) {
                            Text(
                                text = "🎸 ${band.genre}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (band.foundedYear.isNotEmpty()) {
                            Text(
                                text = "📅 Gegründet: ${band.foundedYear}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (isSigning) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Create, null, tint = stageTextColor, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Autogrammstunde", fontSize = 16.sp, color = stageTextColor, fontWeight = FontWeight.Medium)
                    }
                } else {
                    Text(text = "Bühne: ${band.stage}", fontSize = 16.sp, color = stageTextColor, fontWeight = FontWeight.Medium)
                }
                
                Text(
                    text = "Zeit: ${band.formattedTime}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                
                val duration = java.time.Duration.between(band.startTime, band.endTime).toMinutes()
                Text(
                    text = "Dauer: $duration Minuten",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Schließen", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun BandRow(
    band: FestivalBand,
    onToggleFavorite: (String) -> Unit,
    onCardClick: () -> Unit
) {
    val isLightTheme = MaterialTheme.colorScheme.background.luminance() > 0.5f
    val stageTextColor = if (isLightTheme) Color(0xFF1B5E20) else Color(0xFF81C784)
    val textColor = if (isLightTheme) Color.Black else Color.White
    val uriHandler = LocalUriHandler.current

    val isSigning = band.type == EventType.SIGNING
    val cardBgColor = if (isSigning) {
        if (isLightTheme) Color(0xFFF5F5F5) else Color(0xFF2C2C2C)
    } else {
        if (isLightTheme) Color.White else MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onCardClick() },
        colors = CardDefaults.cardColors(containerColor = cardBgColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                val displayName = if (isSigning) band.name.removeSuffix(" (Signing)") else band.name
                Text(text = displayName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textColor)
                if (isSigning) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Create, null, tint = stageTextColor, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Autogrammstunde", fontSize = 12.sp, color = stageTextColor)
                    }
                } else {
                    Text(text = band.stage, fontSize = 12.sp, color = stageTextColor)
                }
            }

            if (!isSigning) {
                Text(
                    text = "🎧",
                    fontSize = 22.sp,
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentHeight(Alignment.CenterVertically)
                        .clickable {
                            val url = "https://open.spotify.com/search/" + band.name.replace(" ", "%20")
                            uriHandler.openUri(url)
                        }
                        .padding(horizontal = 12.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = band.formattedTime, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = textColor)
                IconButton(onClick = { onToggleFavorite(band.id) }) {
                    Icon(
                        imageVector = if (band.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorit",
                        tint = if (band.isFavorite) MaterialTheme.colorScheme.primary else Color.Gray,
                    )
                }
            }
        }
    }
}

@Composable
fun CompactBandCard(
    band: FestivalBand,
    modifier: Modifier = Modifier,
    isConflict: Boolean = false,
    onClick: () -> Unit = {}
) {
    val isLightTheme = MaterialTheme.colorScheme.background.luminance() > 0.5f
    val stageTextColor = if (isLightTheme) Color(0xFF1B5E20) else Color(0xFF81C784)
    val textColor = if (isLightTheme) Color.Black else Color.White
    val uriHandler = LocalUriHandler.current
    val isSigning = band.type == EventType.SIGNING
    val cardBgColor = if (isSigning) {
        if (isLightTheme) Color(0xFFF5F5F5) else Color(0xFF2C2C2C)
    } else {
        if (isLightTheme) Color.White else MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = (if (isConflict) modifier else modifier.padding(vertical = 4.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isConflict) 12.dp else 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f), 
                verticalArrangement = Arrangement.Center
            ) {
                val displayName = if (isSigning) band.name.removeSuffix(" (Signing)") else band.name
                Text(
                    text = displayName,
                    fontSize = if (isConflict) 16.sp else 18.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = textColor, 
                    maxLines = 2,
                    lineHeight = if (isConflict) 18.sp else 20.sp
                )
                if (isSigning) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Create, null, tint = stageTextColor, modifier = Modifier.size(if (isConflict) 12.dp else 14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Autogramm", fontSize = if (isConflict) 11.sp else 12.sp, color = stageTextColor)
                    }
                } else {
                    Text(text = band.stage, fontSize = if (isConflict) 11.sp else 12.sp, color = stageTextColor)
                }
                if (isConflict) {
                    Text(
                        text = band.formattedTime, 
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium, 
                        color = textColor
                    )
                }
            }
            if (!isSigning) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(if (isConflict) 40.dp else 50.dp)
                        .clickable {
                            val url = "https://open.spotify.com/search/" + band.name.replace(" ", "%20")
                            uriHandler.openUri(url)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🎧", fontSize = if (isConflict) 20.sp else 24.sp)
                }
            }
            if (!isConflict) {
                Text(
                    text = band.formattedTime, 
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium, 
                    color = textColor,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
