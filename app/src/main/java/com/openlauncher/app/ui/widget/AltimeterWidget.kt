package com.openlauncher.app.ui.widget

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

@Composable
fun AltimeterWidget(
    accent: Color,
    isDayMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val iconTint = if (isDayMode) Color(0xFF333333) else Color.White.copy(alpha = 0.85f)
    val labelColor = if (isDayMode) Color(0xFF888888) else Color.White.copy(alpha = 0.30f)
    val context = LocalContext.current
    var rollDeg  by remember { mutableFloatStateOf(0f) }
    var pitchDeg by remember { mutableFloatStateOf(0f) }
    val gravBuf  = remember { FloatArray(3) { 0f } }

    DisposableEffect(Unit) {
        val sm = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val alpha = 0.12f
                gravBuf[0] = alpha * event.values[0] + (1f - alpha) * gravBuf[0]
                gravBuf[1] = alpha * event.values[1] + (1f - alpha) * gravBuf[1]
                gravBuf[2] = alpha * event.values[2] + (1f - alpha) * gravBuf[2]
                rollDeg  = Math.toDegrees(
                    atan2(gravBuf[1].toDouble(), gravBuf[2].toDouble())
                ).toFloat()
                pitchDeg = Math.toDegrees(
                    atan2(-gravBuf[0].toDouble(),
                        sqrt(gravBuf[1].toDouble().pow(2) + gravBuf[2].toDouble().pow(2)))
                ).toFloat()
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            sm.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI)
        }
        onDispose { sm.unregisterListener(listener) }
    }

    Box(
        modifier          = modifier,
        contentAlignment  = Alignment.Center
    ) {
        Icon(
            imageVector        = Icons.Default.DirectionsCar,
            contentDescription = null,
            tint               = iconTint,
            modifier           = Modifier
                .size(64.dp)
                .graphicsLayer { rotationZ = rollDeg }
        )

        Row(
            modifier              = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.Bottom
        ) {
            Column {
                Text("ROLL",  color = labelColor, fontSize = 7.sp, letterSpacing = 1.sp)
                Text("%.1f°".format(rollDeg),  color = accent, fontSize = 13.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("PITCH", color = labelColor, fontSize = 7.sp, letterSpacing = 1.sp, textAlign = TextAlign.End)
                Text("%.1f°".format(pitchDeg), color = accent, fontSize = 13.sp, textAlign = TextAlign.End)
            }
        }
    }
}
