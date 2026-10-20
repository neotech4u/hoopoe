package com.hoopoe.launcher.ui.screen

import android.net.Uri
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.hoopoe.launcher.R

@Composable
fun LogonScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var hasFinished by remember { mutableStateOf(false) }

    fun finishSafely() {
        if (!hasFinished) {
            hasFinished = true
            onFinished()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                finishSafely()
            }
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                VideoView(ctx).apply {
                    val uri = Uri.parse("android.resource://${ctx.packageName}/${R.raw.hoopoe_logon}")
                    setVideoURI(uri)
                    setOnCompletionListener {
                        finishSafely()
                    }
                    setOnErrorListener { _, _, _ ->
                        finishSafely()
                        true
                    }
                    start()
                }
            }
        )

        // Subtle skip cue in corner for driver convenience
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = "TAP TO SKIP",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 10.sp,
                letterSpacing = 1.sp
            )
        }
    }
}
