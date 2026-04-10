package com.foss.vidoplay.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class SeekSide { LEFT, RIGHT, NONE }


@Composable
fun SeekRippleOverlay(
    side: SeekSide,
    seekSeconds: Int,
    triggerKey: Any,                      // 👈 New: forces restart on each double‑tap
    modifier: Modifier = Modifier,
    accentColor: Color = Color(0xFFFFFFFF),
) {
    val visible = side != SeekSide.NONE
    val isLeft  = side == SeekSide.LEFT

    // Finite animation controllers – they run only once per triggerKey change
    val ring1 = remember { Animatable(0f) }
    val ring2 = remember { Animatable(0f) }
    val ring3 = remember { Animatable(0f) }

    // Restart the animation whenever triggerKey changes (i.e., a new double‑tap occurs)
    LaunchedEffect(triggerKey) {
        if (visible) {
            // Snap to 0 then animate to 1 with staggered delays
            ring1.snapTo(0f)
            ring2.snapTo(0f)
            ring3.snapTo(0f)

            // Launch animations concurrently with different delays
            launch { ring1.animateTo(1f, tween(700, easing = FastOutSlowInEasing)) }
            launch { delay(160); ring2.animateTo(1f, tween(700, easing = FastOutSlowInEasing)) }
            launch { delay(320); ring3.animateTo(1f, tween(700, easing = FastOutSlowInEasing)) }
        }
    }

    // Auto‑hide the overlay after a short display time
    var internalVisible by remember { mutableStateOf(false) }
    LaunchedEffect(triggerKey) {
        if (visible) {
            internalVisible = true
            delay(900) // Show for ~0.9 seconds (rings finish around 700+ms)
            internalVisible = false
        }
    }

    AnimatedVisibility(
        visible = internalVisible,
        enter = fadeIn(tween(80)) + scaleIn(initialScale = 0.88f, animationSpec = tween(120)),
        exit  = fadeOut(tween(300)),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.38f)
                .clip(
                    if (isLeft) RoundedCornerShape(topEnd = 200.dp, bottomEnd = 200.dp)
                    else         RoundedCornerShape(topStart = 200.dp, bottomStart = 200.dp)
                )
                .background(Color.Black.copy(alpha = 0.28f)),
            contentAlignment = Alignment.Center
        ) {
            // Arc ripple rings drawn on Canvas – values come from finite animatables
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = if (isLeft) size.width * 0.72f else size.width * 0.28f
                val cy = size.height / 2f
                val baseR = size.minDimension * 0.38f

                listOf(ring1.value, ring2.value, ring3.value).forEach { progress ->
                    if (progress > 0f) {
                        val r     = baseR * (0.5f + progress * 0.7f)
                        val alpha = (1f - progress).coerceIn(0f, 1f) * 0.55f
                        drawCircle(
                            color  = accentColor.copy(alpha = alpha),
                            radius = r,
                            center = Offset(cx, cy),
                            style  = Stroke(width = 2.5.dp.toPx())
                        )
                    }
                }

                // Filled glow circle at origin
                drawCircle(
                    brush  = Brush.radialGradient(
                        listOf(accentColor.copy(0.18f), Color.Transparent),
                        Offset(cx, cy)
                    ),
                    radius = baseR * 0.55f,
                    center = Offset(cx, cy)
                )
            }

            // Icon + label column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(
                    start  = if (isLeft) 0.dp else 16.dp,
                    end    = if (isLeft) 16.dp else 0.dp
                )
            ) {
                Icon(
                    imageVector = if (isLeft) Icons.Default.FastRewind else Icons.Default.FastForward,
                    contentDescription = null,
                    tint     = Color.White,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text       = "${seekSeconds}s",
                    color      = Color.White,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SpeedGestureBadge(
    modifier: Modifier = Modifier,
    visible: Boolean,
    boostSpeed: Float = 2f,
    boostDurationMs: Long = 0L,
    accentColor: Color = Color(0xFFE50914)
) {
    val progressAnim = remember { Animatable(1f) }
    LaunchedEffect(visible, boostDurationMs) {
        if (visible && boostDurationMs > 0L) {
            progressAnim.snapTo(1f)
            progressAnim.animateTo(
                0f,
                animationSpec = tween(boostDurationMs.toInt(), easing = LinearEasing)
            )
        } else if (!visible) {
            progressAnim.snapTo(1f)
        }
    }

    val inf = rememberInfiniteTransition(label = "speedPulse")
    val iconScale by inf.animateFloat(
        initialValue = 1f, targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "iconScale"
    )

    AnimatedVisibility(
        visible  = visible,
        enter    = fadeIn(tween(100)) + scaleIn(
            initialScale  = 0.7f,
            animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessHigh)
        ),
        exit     = fadeOut(tween(220)) + scaleOut(
            targetScale   = 0.75f,
            animationSpec = tween(200)
        ),
        modifier = modifier
    ) {
        Surface(
            color         = Color.Black.copy(alpha = 0.72f),
            shape         = RoundedCornerShape(40.dp),
            modifier      = Modifier.padding(top = 18.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier            = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = null,
                        tint     = accentColor,
                        modifier = Modifier
                            .size(18.dp)
                            .graphicsLayer { scaleX = iconScale; scaleY = iconScale }
                    )
                    Text(
                        text       = "${boostSpeed}× Speed",
                        color      = Color.White,
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.3.sp
                    )
                }

                if (boostDurationMs > 0L) {
                    Spacer(Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(2.dp)
                            .clip(RoundedCornerShape(1.dp))
                            .background(Color.White.copy(0.2f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progressAnim.value)
                                .fillMaxHeight()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(accentColor.copy(0.7f), accentColor)
                                    )
                                )
                        )
                    }
                }
            }
        }
    }
}