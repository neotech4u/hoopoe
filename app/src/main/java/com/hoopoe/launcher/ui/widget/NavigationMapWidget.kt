package com.hoopoe.launcher.ui.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.SoundEffectConstants
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.TurnRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hoopoe.launcher.util.LocationData

@Composable
fun NavigationMapWidget(
    location: LocationData?,
    bearing: Float,
    accent: Color,
    isDayMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current
    val contentColor = if (isDayMode) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val cardBorder = if (isDayMode) Color(0xFFCBD5E1) else Color(0xFF243048)

    // 60FPS Perspective Road Animation
    val infiniteTransition = rememberInfiniteTransition(label = "road_anim")
    val roadOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "road_offset"
    )

    val radarPulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_pulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                launchNavApp(context)
            }
    ) {
        // 1. Perspective Wireframe Road Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            if (w <= 0 || h <= 0) return@Canvas

            val horizon = h * 0.40f
            val centerX = w * 0.5f

            // Ground grid lines
            for (y in (horizon.toInt()..h.toInt() step 22)) {
                drawLine(
                    color = accent.copy(alpha = 0.05f),
                    start = Offset(0f, y.toFloat()),
                    end = Offset(w, y.toFloat()),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Road trapezoid
            val topRoadW = w * 0.12f
            val botRoadW = w * 0.76f

            val pLeftTop = Offset(centerX - topRoadW, horizon)
            val pRightTop = Offset(centerX + topRoadW, horizon)
            val pLeftBot = Offset(centerX - botRoadW, h)
            val pRightBot = Offset(centerX + botRoadW, h)

            val roadPath = Path().apply {
                moveTo(pLeftTop.x, pLeftTop.y)
                lineTo(pRightTop.x, pRightTop.y)
                lineTo(pRightBot.x, pRightBot.y)
                lineTo(pLeftBot.x, pLeftBot.y)
                close()
            }

            drawPath(
                path = roadPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F141F).copy(alpha = 0.4f), Color(0xFF07090E).copy(alpha = 0.95f)),
                    startY = horizon,
                    endY = h
                )
            )

            // Neon Glowing Road Borders
            drawLine(
                color = accent.copy(alpha = 0.85f),
                start = pLeftTop,
                end = pLeftBot,
                strokeWidth = 2.5.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = accent.copy(alpha = 0.85f),
                start = pRightTop,
                end = pRightBot,
                strokeWidth = 2.5.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Animated Center Lane Dash Lines
            for (i in 0..7) {
                val t = ((i * 0.15f + (roadOffset * 0.15f)) % 1f)
                if (t > 0.05f && t < 0.95f) {
                    val y1 = horizon + (t * t) * (h - horizon)
                    val y2 = horizon + (minOf(1f, t + 0.06f) * minOf(1f, t + 0.06f)) * (h - horizon)
                    drawLine(
                        color = Color.White.copy(alpha = 0.45f),
                        start = Offset(centerX, y1),
                        end = Offset(centerX, y2),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            }

            // Vehicle Silhouette & Radar Pulse at Bottom Center
            val carY = h - 48.dp.toPx()
            val carW = 38.dp.toPx()
            val carH = 58.dp.toPx()

            // Radar pulse ring
            val rRadius = 24.dp.toPx() + (radarPulse * 28.dp.toPx())
            drawArc(
                color = accent.copy(alpha = (1f - radarPulse) * 0.5f),
                startAngle = 200f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = Offset(centerX - rRadius, carY - rRadius * 0.3f),
                size = androidx.compose.ui.geometry.Size(rRadius * 2f, rRadius * 2f),
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Vehicle Body Silhouette
            drawRoundRect(
                color = Color(0xFF0F141F),
                topLeft = Offset(centerX - carW / 2f, carY),
                size = androidx.compose.ui.geometry.Size(carW, carH),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx(), 10.dp.toPx())
            )
            drawRoundRect(
                color = accent,
                topLeft = Offset(centerX - carW / 2f, carY),
                size = androidx.compose.ui.geometry.Size(carW, carH),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx(), 10.dp.toPx()),
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Neon Headlights
            drawRect(
                color = accent,
                topLeft = Offset(centerX - carW / 2f + 4.dp.toPx(), carY + 3.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(7.dp.toPx(), 3.dp.toPx())
            )
            drawRect(
                color = accent,
                topLeft = Offset(centerX + carW / 2f - 11.dp.toPx(), carY + 3.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(7.dp.toPx(), 3.dp.toPx())
            )
        }

        // 2. Turn-by-Turn Guidance Banner Overlay (Top)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Guidance Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F141F).copy(alpha = 0.90f))
                    .border(1.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(accent.copy(alpha = 0.15f))
                        .border(1.dp, accent, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.TurnRight,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "IN 250 METERS",
                        color = accent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Turn Right onto Expressway",
                        color = contentColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Speed Limit Badge (80)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(3.dp, Color(0xFFFF1744), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "80",
                        color = Color(0xFF1E293B),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Bottom Action Bar: Open OSM / Maps
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Open Maps Quick Button
                Button(
                    onClick = {
                        view.playSoundEffect(SoundEffectConstants.CLICK)
                        launchNavApp(context)
                    },
                    modifier = Modifier.height(38.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accent),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(
                        Icons.Default.Explore,
                        contentDescription = null,
                        tint = Color(0xFF07090E),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "OPEN OSM / MAPS",
                        color = Color(0xFF07090E),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                // ADAS Status Indicator
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0F141F).copy(alpha = 0.85f))
                        .border(1.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(accent)
                        )
                        Text(
                            text = "LANE PILOT",
                            color = accent,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

fun launchNavApp(context: Context) {
    val packages = listOf(
        "org.osmand",
        "org.osmand.plus",
        "app.organicmaps",
        "com.mapswithme.maps.pro",
        "com.google.android.apps.maps"
    )
    for (pkg in packages) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(pkg)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
            return
        }
    }

    try {
        val geoIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=navigation")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(geoIntent)
    } catch (_: Exception) {}
}
