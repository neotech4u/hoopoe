package com.hoopoe.launcher.ui.widget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hoopoe.launcher.util.LocationData
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SpeedometerWidget(
    location: LocationData?,
    isMetric: Boolean,
    accent: Color,
    isDayMode: Boolean = false,
    digitalOnly: Boolean = false,
    modifier: Modifier = Modifier
) {
    val maxSpeed     = if (isMetric) 200f else 124f
    val speedDisplay = ((location?.speedMps ?: 0f) * if (isMetric) 3.6f else 2.237f).coerceAtLeast(0f)
    val unitLabel    = if (isMetric) "KM/H" else "MPH"
    val trackAlpha   = if (isDayMode) 0.18f else 0.12f

    val contentColor = if (isDayMode) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val subAlpha     = if (isDayMode) 0.65f else 0.55f
    val tickBaseColor = if (isDayMode) Color(0xFF334155) else Color(0xFF94A3B8)

    // Interactive Gear State (Defaults to Drive "D")
    var currentGear by remember { mutableStateOf("D") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Sub-header: Cluster Title
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TELEMETRY",
                color = contentColor.copy(alpha = 0.45f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Text(
                text = "GPS ACTIVE",
                color = accent,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        // Center: Radial Speedometer Dial or Digital Readout
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (digitalOnly) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text          = "%.0f".format(speedDisplay),
                        color         = contentColor,
                        fontSize      = 64.sp,
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = (-2).sp
                    )
                    Text(
                        text          = unitLabel,
                        color         = accent,
                        fontSize      = 11.sp,
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = 2.5.sp
                    )
                }
            } else {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx         = size.width  / 2f
                    val cy         = size.height / 2f
                    val arcR       = minOf(size.width, size.height) * 0.40f
                    val trackW     = arcR * 0.12f
                    val startAngle = 145f
                    val sweepTotal = 250f
                    val progressSweep = (speedDisplay / maxSpeed).coerceIn(0f, 1f) * sweepTotal

                    val tl = Offset(cx - arcR, cy - arcR)
                    val sz = Size(arcR * 2f, arcR * 2f)

                    // Track Background
                    drawArc(
                        color      = contentColor.copy(alpha = trackAlpha),
                        startAngle = startAngle,
                        sweepAngle = sweepTotal,
                        useCenter  = false,
                        topLeft    = tl,
                        size       = sz,
                        style      = Stroke(width = trackW, cap = StrokeCap.Round)
                    )

                    // Active Glowing Speed Progress Arc
                    if (progressSweep > 1f) {
                        drawArc(
                            color      = accent,
                            startAngle = startAngle,
                            sweepAngle = progressSweep,
                            useCenter  = false,
                            topLeft    = tl,
                            size       = sz,
                            style      = Stroke(width = trackW, cap = StrokeCap.Round)
                        )
                    }

                    // Automotive Dial Ticks
                    for (i in 0..10) {
                        val angle   = startAngle + i * (sweepTotal / 10f)
                        val rad     = Math.toRadians(angle.toDouble())
                        val isMajor = i % 2 == 0
                        val outerR  = arcR - trackW / 2f - 4.dp.toPx()
                        val innerR  = outerR - if (isMajor) 7.dp.toPx() else 4.dp.toPx()
                        drawLine(
                            color       = tickBaseColor.copy(alpha = if (isMajor) 0.6f else 0.3f),
                            start       = Offset(cx + (outerR * cos(rad)).toFloat(), cy + (outerR * sin(rad)).toFloat()),
                            end         = Offset(cx + (innerR * cos(rad)).toFloat(), cy + (innerR * sin(rad)).toFloat()),
                            strokeWidth = if (isMajor) 2.dp.toPx() else 1.dp.toPx()
                        )
                    }
                }

                // Digital Center Readout
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text          = "%.0f".format(speedDisplay),
                        color         = contentColor,
                        fontSize      = 44.sp,
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = (-1.5).sp
                    )
                    Text(
                        text          = unitLabel,
                        color         = accent,
                        fontSize      = 10.sp,
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }
            }
        }

        // Bottom: Tactile PRND Gear Selector Bar
        Row(
            modifier = Modifier
                .padding(bottom = 6.dp)
                .background(Color.Black.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                .border(1.dp, Color(0xFF243048), RoundedCornerShape(20.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("P", "R", "N", "D").forEach { gear ->
                val isActive = currentGear == gear
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(if (isActive) accent else Color.Transparent)
                        .clickable { currentGear = gear },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = gear,
                        color = if (isActive) Color(0xFF07090E) else contentColor.copy(alpha = 0.45f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
