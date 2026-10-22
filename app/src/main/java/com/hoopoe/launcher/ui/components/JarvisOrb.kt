package com.hoopoe.launcher.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.SoundEffectConstants
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Jarvis-like Smart Assistant Orb:
 * A subtle, futuristic, holographic arc-reactor animation.
 * Compact and non-distracting for in-car heads-up environments.
 * Tapping directly triggers Google Assistant / Voice Search.
 */
@Composable
fun JarvisOrb(
    accent: Color,
    modifier: Modifier = Modifier,
    orbSize: Dp = 38.dp,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val view = LocalView.current

    // Subtle Continuous Holographic Arc Reactor Animations
    val infiniteTransition = rememberInfiniteTransition(label = "jarvis_anim")

    // Slow outer ring rotation
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Gentle core pulse (scale 0.88 -> 1.08)
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Alpha ripple glow
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = modifier
            .size(orbSize)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                if (onClick != null) {
                    onClick()
                } else {
                    launchGoogleAssistant(context)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Holographic Arc Reactor Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = (minOf(size.width, size.height) / 2f) - 2.dp.toPx()

            // 1. Subtle Outer Ambient Glow Ring
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(accent.copy(alpha = glowAlpha * 0.45f), Color.Transparent),
                    center = center,
                    radius = radius * 1.25f
                ),
                radius = radius * 1.15f,
                center = center
            )

            // 2. Rotating Segmented Arc Reactor Rings
            val arcLength = 65f
            val strokeW = 1.75.dp.toPx()
            for (i in 0..2) {
                val startAngle = rotation + (i * 120f)
                drawArc(
                    color = accent.copy(alpha = 0.85f),
                    startAngle = startAngle,
                    sweepAngle = arcLength,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f),
                    style = Stroke(width = strokeW, cap = StrokeCap.Round)
                )
            }

            // 3. Inner Pulsing Core
            val innerR = radius * 0.52f * pulseScale
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = 0.95f), accent.copy(alpha = 0.6f), Color.Transparent),
                    center = center,
                    radius = innerR * 1.4f
                ),
                radius = innerR,
                center = center
            )

            // Inner Ring Border
            drawCircle(
                color = accent.copy(alpha = glowAlpha),
                radius = innerR,
                center = center,
                style = Stroke(width = 1.2.dp.toPx())
            )
        }

        // Center Subtle Mic / AI Icon
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "Jarvis Assistant",
            tint = Color(0xFF07090E),
            modifier = Modifier.size(orbSize * 0.38f)
        )
    }
}

fun launchGoogleAssistant(context: Context) {
    try {
        val voiceIntent = Intent(Intent.ACTION_VOICE_COMMAND).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(voiceIntent)
    } catch (_: Exception) {
        try {
            val assistIntent = Intent("android.intent.action.ASSIST").apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(assistIntent)
        } catch (_: Exception) {
            try {
                val searchIntent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(searchIntent)
            } catch (_: Exception) {}
        }
    }
}
