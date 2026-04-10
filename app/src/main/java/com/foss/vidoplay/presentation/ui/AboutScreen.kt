package com.foss.vidoplay.presentation.ui

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Screenshot
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.foss.vidoplay.R
import com.foss.vidoplay.presentation.common.GlassTokens
import com.foss.vidoplay.presentation.common.glassCard
import com.foss.vidoplay.presentation.common.glassChip
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(innerPadding: PaddingValues, onBack: () -> Unit) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Animation states
    var logoScale by remember { mutableFloatStateOf(1f) }
    var showContent by remember { mutableStateOf(false) }

    // Dynamic colors
    val textPrimary = GlassTokens.getTextPrimary()
    val textSecondary = GlassTokens.getTextSecondary()
    val textTertiary = GlassTokens.getTextTertiary()
    val chipBg = GlassTokens.getChipBg()
    val chipBorder = GlassTokens.getChipBorder()
    val isDark = GlassTokens.isDarkTheme()
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    // Animate logo on startup
    LaunchedEffect(Unit) {
        logoScale = 1.2f
        delay(200)
        logoScale = 1f
        delay(300)
        showContent = true
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.about_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = textPrimary
                    )
                },
                navigationIcon = {
                    // Glass back button
                    Surface(
                        modifier = Modifier
                            .size(40.dp)
                            .glassChip(cornerRadius = 12.dp)
                            .clickable { onBack() },
                        color = Color.Transparent
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back),
                                tint = primaryColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Animated background circles
            GlassAnimatedBackgroundCircles()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // App Logo with animation
                GlassAnimatedLogo(logoScale)

                Spacer(modifier = Modifier.height(16.dp))

                // App Name
                Text(
                    text = stringResource(R.string.app_name),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Version chip
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = primaryColor.copy(alpha = 0.15f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.version, "2.3"),
                        fontSize = 14.sp,
                        color = primaryColor,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Animated content
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn() + slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(500, easing = FastOutSlowInEasing)
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Description card
                        GlassDescriptionCard()

                        Spacer(modifier = Modifier.height(24.dp))

                        // Features Section
                        Text(
                            text = stringResource(R.string.key_features),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            color = primaryColor
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        GlassFeatureItem(
                            icon = Icons.Default.PlayArrow,
                            title = stringResource(R.string.high_quality_playback),
                            description = stringResource(R.string.high_quality_desc)
                        )
                        GlassFeatureItem(
                            icon = Icons.Default.PictureInPicture,
                            title = stringResource(R.string.picture_in_picture),
                            description = stringResource(R.string.pip_desc)
                        )
                        GlassFeatureItem(
                            icon = Icons.Default.Speed,
                            title = stringResource(R.string.variable_speed),
                            description = stringResource(R.string.variable_speed_desc)
                        )
                        GlassFeatureItem(
                            icon = Icons.Default.GraphicEq,
                            title = stringResource(R.string.audio_controls),
                            description = stringResource(R.string.audio_controls_desc)
                        )
                        GlassFeatureItem(
                            icon = Icons.Default.Screenshot,
                            title = stringResource(R.string.screenshots),
                            description = stringResource(R.string.screenshots_desc)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Connect Section
                        Text(
                            text = stringResource(R.string.connect_with_us),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            color = primaryColor
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            GlassSocialButton(
                                icon = Icons.Default.Email,
                                label = stringResource(R.string.email),
                                color = Color(0xFFEA4335),
                                onClick = {
                                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = "mailto:support@vidoplay.com".toUri()
                                        putExtra(Intent.EXTRA_SUBJECT, "Share Vido Player")
                                    }
                                    context.startActivity(intent)
                                }
                            )
                            GlassSocialButton(
                                icon = Icons.Default.Star,
                                label = stringResource(R.string.rate),
                                color = Color(0xFFFFC107),
                                onClick = {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        "market://details?id=${context.packageName}".toUri()
                                    )
                                    context.startActivity(intent)
                                }
                            )
                            GlassSocialButton(
                                icon = Icons.Default.Share,
                                label = stringResource(R.string.share),
                                color = Color(0xFF25D366),
                                onClick = {
                                    val shareIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, "share")
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "share"))
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Legal Section
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Text(
                                text = "© 2024 VidoPlay",
                                fontSize = 12.sp,
                                color = textTertiary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.made_with_love),
                                fontSize = 12.sp,
                                color = textTertiary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = { /* Open Privacy Policy */ }) {
                                    Text(stringResource(R.string.privacy_policy), fontSize = 12.sp, color = textSecondary)
                                }
                                Text("•", color = textTertiary)
                                TextButton(onClick = { /* Open Terms */ }) {
                                    Text(stringResource(R.string.terms_of_service), fontSize = 12.sp, color = textSecondary)
                                }
                                Text("•", color = textTertiary)
                                TextButton(onClick = { /* Open Licenses */ }) {
                                    Text(stringResource(R.string.open_source_licenses), fontSize = 12.sp, color = textSecondary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== GLASS DESCRIPTION CARD ====================

@Composable
private fun GlassDescriptionCard() {
    val textSecondary = GlassTokens.getTextSecondary()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .glassCard(cornerRadius = 16.dp),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.your_ultimate_video_player),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.app_description),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = textSecondary,
                lineHeight = 20.sp
            )
        }
    }
}

// ==================== GLASS FEATURE ITEM ====================

@Composable
private fun GlassFeatureItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    val textPrimary = GlassTokens.getTextPrimary()
    val textSecondary = GlassTokens.getTextSecondary()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .glassCard(cornerRadius = 12.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = textPrimary
                )
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = textSecondary
                )
            }
        }
    }
}

// ==================== GLASS SOCIAL BUTTON ====================

@Composable
private fun GlassSocialButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    val textSecondary = GlassTokens.getTextSecondary()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = color.copy(alpha = 0.15f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = textSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}

// ==================== GLASS ANIMATED BACKGROUND CIRCLES ====================

@Composable
private fun GlassAnimatedBackgroundCircles() {
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier.fillMaxSize()
    )

    // Floating circles with glass effect
    repeat(3) { index ->
        val infiniteTransition = rememberInfiniteTransition()
        val offsetX by infiniteTransition.animateFloat(
            initialValue = -100f,
            targetValue = 100f,
            animationSpec = infiniteRepeatable(
                animation = tween(8000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
        val offsetY by infiniteTransition.animateFloat(
            initialValue = -50f,
            targetValue = 50f,
            animationSpec = infiniteRepeatable(
                animation = tween(10000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        Box(
            modifier = Modifier
                .offset(x = offsetX.dp, y = offsetY.dp)
                .size((150 + index * 50).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.06f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )
    }
}

// ==================== GLASS ANIMATED LOGO ====================

@Composable
private fun GlassAnimatedLogo(scale: Float) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Box(
        modifier = Modifier
            .size(120.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        primaryColor,
                        secondaryColor
                    )
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.3f),
                        Color.White.copy(alpha = 0.1f)
                    )
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.play_button),
            contentDescription = stringResource(R.string.app_logo),
            tint = Color.White,
            modifier = Modifier.size(64.dp)
        )
    }
}