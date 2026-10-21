package com.hoopoe.launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.hoopoe.launcher.data.TripFuelRepository

@Composable
fun RefuelDialog(
    accent: Color,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val repo = remember { TripFuelRepository.getInstance(context) }
    val isObdOnline by repo.isObdOnline.collectAsState()
    val defaultPrice by repo.fuelPricePerLiter.collectAsState()
    val currentOdo by repo.odometerKm.collectAsState()

    var inputLiters by remember { mutableStateOf("25.0") }
    var odoInput by remember { mutableStateOf(currentOdo.toInt().toString()) }
    var isFullTank by remember { mutableStateOf(false) }
    var isSaved by remember { mutableStateOf(false) }

    val litersVal = inputLiters.toDoubleOrNull() ?: 0.0
    val totalCost = litersVal * defaultPrice.toDouble()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.82f))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 760.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF0F141F).copy(alpha = 0.95f))
                    .border(1.5.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(22.dp))
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(accent.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.LocalGasStation,
                                    contentDescription = null,
                                    tint = accent,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "QUICK REFUEL LOG",
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = if (isObdOnline) "OBD-II CANBUS SYNCED (PID 0x2F)" else "STANDALONE / MANUAL FALLBACK",
                                    color = if (isObdOnline) accent else Color(0xFFFF9100),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.08f))
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    HorizontalDivider(color = Color(0xFF243048))

                    // Split Layout: Controls & Keypad
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        // Left Column: Values & Quick Presets
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Liters Display Box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.Black.copy(alpha = 0.45f))
                                    .border(1.dp, Color(0xFF243048), RoundedCornerShape(14.dp))
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "VOLUME ADDED",
                                        color = Color(0xFF64748B),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                    Row(
                                        verticalAlignment = Alignment.Bottom,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = if (inputLiters.isEmpty()) "0" else inputLiters,
                                            color = accent,
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            text = "LITERS",
                                            color = Color(0xFF94A3B8),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(bottom = 5.dp)
                                        )
                                    }
                                }
                            }

                            // Total Cost & Odometer Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.Black.copy(alpha = 0.35f))
                                        .border(1.dp, Color(0xFF243048), RoundedCornerShape(12.dp))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "TOTAL COST",
                                            color = Color(0xFF64748B),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "₹%.2f".format(totalCost),
                                            color = Color(0xFF00E676),
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.Black.copy(alpha = 0.35f))
                                        .border(1.dp, Color(0xFF243048), RoundedCornerShape(12.dp))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "ODOMETER",
                                            color = Color(0xFF64748B),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "$odoInput KM",
                                            color = Color.White,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }

                            // Quick Presets
                            Text(
                                text = "QUICK AMOUNT PRESETS",
                                color = Color(0xFF64748B),
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("1000", "2000", "3000").forEach { amount ->
                                    val litersCalc = "%.1f".format(amount.toDouble() / defaultPrice)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color.White.copy(alpha = 0.05f))
                                            .border(1.dp, Color(0xFF243048), RoundedCornerShape(10.dp))
                                            .clickable {
                                                TripFuelRepository.playTactileBeep()
                                                inputLiters = litersCalc
                                                isFullTank = false
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "₹$amount",
                                            color = Color.White,
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1.1f)
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(accent.copy(alpha = 0.15f))
                                        .border(1.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                        .clickable {
                                            TripFuelRepository.playTactileBeep()
                                            inputLiters = "42.0"
                                            isFullTank = true
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "FULL TANK",
                                        color = accent,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }

                            Spacer(Modifier.weight(1f))

                            // One-Tap Save Log Action Button
                            Button(
                                onClick = {
                                    if (!isSaved && litersVal > 0.0) {
                                        isSaved = true
                                        repo.addRefuel(
                                            liters = litersVal,
                                            costPerLiter = defaultPrice.toDouble(),
                                            odometer = (odoInput.toDoubleOrNull() ?: currentOdo),
                                            isFullTank = isFullTank
                                        )
                                        onDismiss()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF00E676)
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF07090E),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = if (isSaved) "SAVED ✓" else "CONFIRM & SAVE LOG",
                                    color = Color(0xFF07090E),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        // Right Column: NHTSA Ergonomic >= 64px Keypad
                        Column(
                            modifier = Modifier.width(280.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val keys = listOf(
                                listOf("1", "2", "3"),
                                listOf("4", "5", "6"),
                                listOf("7", "8", "9"),
                                listOf(".", "0", "⌫")
                            )

                            keys.forEach { row ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    row.forEach { k ->
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(64.dp) // NHTSA >= 64px touch standard
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(Color.White.copy(alpha = 0.06f))
                                                .border(1.dp, Color(0xFF243048), RoundedCornerShape(14.dp))
                                                .clickable {
                                                    TripFuelRepository.playTactileBeep()
                                                    if (k == "⌫") {
                                                        if (inputLiters.isNotEmpty()) {
                                                            inputLiters = inputLiters.dropLast(1)
                                                        }
                                                    } else if (k == ".") {
                                                        if (!inputLiters.contains(".")) {
                                                            inputLiters += "."
                                                        }
                                                    } else {
                                                        if (inputLiters == "0" || inputLiters == "25.0") {
                                                            inputLiters = k
                                                        } else if (inputLiters.length < 5) {
                                                            inputLiters += k
                                                        }
                                                    }
                                                    isFullTank = false
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = k,
                                                color = if (k == "⌫") Color(0xFFFF9100) else Color.White,
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
