package com.foss.vidoplay.presentation.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foss.vidoplay.R
import com.foss.vidoplay.presentation.common.GlassTokens
import com.foss.vidoplay.presentation.viewModel.ThemeViewModel
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val themeViewModel: ThemeViewModel = koinViewModel()

    LaunchedEffect(key1 = true) {
        delay(2000)
        onSplashFinished()
    }

    val infiniteTransition = rememberInfiniteTransition()

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val textAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val glassGlow by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val isDark = themeViewModel.isDarkThemeActive()
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = if (isDark) {
                        listOf(
                            primaryColor.copy(alpha = 0.15f),
                            Color(0xFF0A0A0A),
                            Color(0xFF050505)
                        )
                    } else {
                        listOf(
                            primaryColor.copy(alpha = 0.08f),
                            Color(0xFFF5F5F5),
                            Color(0xFFEEEEEE)
                        )
                    },
                    radius = 1.5f,
                    center = Offset(0.5f, 0.4f)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Animated glass background circles
        GlassSplashBackgroundCircles(isDark = isDark, primaryColor = primaryColor)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Glass logo container
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.25f),
                                primaryColor.copy(alpha = 0.1f)
                            )
                        )
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.3f),
                                primaryColor.copy(alpha = 0.5f),
                                Color.White.copy(alpha = 0.1f)
                            )
                        ),
                        shape = CircleShape
                    )
                    .scale(scale),
                contentAlignment = Alignment.Center
            ) {
                // Glowing effect behind logo
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    primaryColor.copy(alpha = glassGlow * 0.5f),
                                    Color.Transparent
                                )
                            )
                        )
                        .blur(12.dp)
                )

                Text(
                    text = stringResource(R.string.splash_play_symbol),
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Glass text with gradient
            Text(
                text = stringResource(R.string.vidoPlay),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Glass tagline
            Text(
                text = stringResource(R.string.splash_tagline),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDark) Color.White.copy(alpha = 0.8f) else Color.Gray.copy(alpha = 0.8f),
                modifier = Modifier.alpha(textAlpha * 0.9f),
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Glass loading dots
            Row {
                repeat(3) { index ->
                    GlassLoadingDot(
                        index = index,
                        primaryColor = primaryColor
                    )

                    if (index < 2) {
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun GlassSplashBackgroundCircles(
    isDark: Boolean,
    primaryColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition()

    val circle1Scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val circle2Scale by infiniteTransition.animateFloat(
        initialValue = 1.1f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val circle3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .size(300.dp)
            .scale(circle1Scale)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = if (isDark) 0.08f else 0.05f),
                        Color.Transparent
                    )
                )
            )
            .offset(x = (-100).dp, y = (-100).dp)
    )

    Box(
        modifier = Modifier
            .size(250.dp)
            .scale(circle2Scale)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = if (isDark) 0.06f else 0.04f),
                        Color.Transparent
                    )
                )
            )
            .offset(x = 100.dp, y = 100.dp)
    )

    Box(
        modifier = Modifier
            .size(150.dp)
            .alpha(circle3Alpha)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.08f),
                        Color.Transparent
                    )
                )
            )
            .offset(x = 120.dp, y = (-150).dp)
    )
}

@Composable
private fun GlassLoadingDot(
    index: Int,
    primaryColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition()

    val dotScale by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )


    val finalScale = when (index) {
        0 -> dotScale
        1 -> dotScale.coerceAtLeast(0.8f)
        else -> dotScale.coerceAtMost(1.0f)
    }

    Box(
        modifier = Modifier
            .size(10.dp)
            .scale(finalScale)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor,
                        primaryColor.copy(alpha = 0.6f)
                    )
                )
            )
            .alpha(dotAlpha)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.2f),
                shape = CircleShape
            )
    )
}