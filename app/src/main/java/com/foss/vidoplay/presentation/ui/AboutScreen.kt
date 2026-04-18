package com.foss.vidoplay.presentation.ui

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.foss.vidoplay.R
import com.foss.vidoplay.presentation.common.GlassTokens
import com.foss.vidoplay.presentation.common.glassCard
import com.foss.vidoplay.presentation.common.glassChip
import kotlinx.coroutines.delay


private val Purple400 = Color(0xFF7C4DFF)
private val Purple200 = Color(0xFFB388FF)
private val Purple800 = Color(0xFF4A148C)
private val Teal400   = Color(0xFF1D9E75)
private val Amber400  = Color(0xFFEF9F27)
private val Blue200   = Color(0xFF85B7EB)
private val Coral200  = Color(0xFFF0997B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(innerPadding: PaddingValues, onBack: () -> Unit) {
    val context     = LocalContext.current
    val scrollState = rememberScrollState()
    val isDark      = GlassTokens.isDarkTheme()

    val textPrimary   = GlassTokens.getTextPrimary()
    val textSecondary = GlassTokens.getTextSecondary()
    val textTertiary  = GlassTokens.getTextTertiary()
    val primaryColor  = MaterialTheme.colorScheme.primary

    // ── Logo entrance animation ──────────────────────────────────────────────
    var logoVisible by remember { mutableStateOf(false) }
    var showContent by remember { mutableStateOf(false) }
    val logoScale by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0.7f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
        label = "logoScale"
    )

    LaunchedEffect(Unit) {
        delay(80); logoVisible = true
        delay(320); showContent = true
    }

    // ── Ambient orb animation ────────────────────────────────────────────────
    val infTrans = rememberInfiniteTransition(label = "orb")
    val orbAlpha by infTrans.animateFloat(
        0.12f, 0.24f,
        infiniteRepeatable(tween(2800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "orbAlpha"
    )

    val bgColor = if (isDark) Color(0xFF0A0A12) else Color(0xFFF0EFF8)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.about_title),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = textPrimary
                    )
                },
                navigationIcon = {
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        },
        containerColor = bgColor
    ) { paddingValues ->

        Box(modifier = Modifier.fillMaxSize()) {

            // ── Ambient background orbs ──────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(360.dp)
                    .offset(x = (-120).dp, y = (-80).dp)
                    .background(
                        Brush.radialGradient(
                            listOf(Purple400.copy(orbAlpha), Color.Transparent)
                        ), CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 80.dp, y = 120.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(Purple200.copy(orbAlpha * 0.6f), Color.Transparent)
                        ), CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = (-40).dp, y = (-160).dp)
                    .background(
                        Brush.radialGradient(
                            listOf(Purple400.copy(orbAlpha * 0.5f), Color.Transparent)
                        ), CircleShape
                    )
            )

            // ── Main content ─────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(bottom = innerPadding.calculateBottomPadding() + 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(Modifier.height(12.dp))

                // ── Logo ─────────────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(logoScale),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer glow ring
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(
                                width = 1.dp,
                                brush = Brush.linearGradient(
                                    listOf(Purple200.copy(0.35f), Color.Transparent, Purple200.copy(0.15f))
                                ),
                                shape = CircleShape
                            )
                    )
                    // Main logo circle
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Purple400, Color(0xFF6200EA), Purple800)
                                )
                            )
                            .border(
                                1.5.dp,
                                Brush.linearGradient(
                                    listOf(Color.White.copy(0.30f), Color.White.copy(0.08f))
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.play_button),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(54.dp)
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // App name
                Text(
                    text = stringResource(R.string.app_name),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color(0xFF1A0050),
                    letterSpacing = 2.sp
                )

                Spacer(Modifier.height(8.dp))

                // Version pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Purple200.copy(0.15f))
                        .border(1.dp, Purple200.copy(0.30f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = stringResource(R.string.version, "2.3"),
                        fontSize = 13.sp,
                        color = Purple200,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(Modifier.height(24.dp))

                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(tween(400)) + slideInVertically(
                        tween(400, easing = FastOutSlowInEasing)
                    ) { it / 3 }
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // ── Description card ──────────────────────────────────
                        AboutGlassCard(modifier = Modifier.padding(horizontal = 20.dp)) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(R.string.your_ultimate_video_player),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Purple200
                                )
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    text = stringResource(R.string.app_description),
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center,
                                    color = textSecondary,
                                    lineHeight = 20.sp
                                )
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        // ── Stats row ─────────────────────────────────────────
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            listOf(
                                Triple("60", "FPS playback", Purple200),
                                Triple("10+", "Features", Teal400),
                                Triple("4K", "Support", Amber400)
                            ).forEach { (value, label, color) ->
                                AboutStatCard(
                                    value = value,
                                    label = label,
                                    color = color,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(Modifier.height(28.dp))

                        // ── Features ──────────────────────────────────────────
                        AboutSectionLabel(
                            text = stringResource(R.string.key_features),
                            color = Purple200
                        )

                        Spacer(Modifier.height(12.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AboutFeatureRow(
                                icon = Icons.Default.PlayArrow,
                                iconColor = Purple200,
                                iconBg = Purple200.copy(0.15f),
                                title = stringResource(R.string.high_quality_playback),
                                description = stringResource(R.string.high_quality_desc)
                            )
                            AboutFeatureRow(
                                icon = Icons.Default.PictureInPicture,
                                iconColor = Teal400,
                                iconBg = Teal400.copy(0.15f),
                                title = stringResource(R.string.picture_in_picture),
                                description = stringResource(R.string.pip_desc)
                            )
                            AboutFeatureRow(
                                icon = Icons.Default.Speed,
                                iconColor = Amber400,
                                iconBg = Amber400.copy(0.15f),
                                title = stringResource(R.string.variable_speed),
                                description = stringResource(R.string.variable_speed_desc)
                            )
                            AboutFeatureRow(
                                icon = Icons.Default.GraphicEq,
                                iconColor = Blue200,
                                iconBg = Blue200.copy(0.15f),
                                title = stringResource(R.string.audio_controls),
                                description = stringResource(R.string.audio_controls_desc)
                            )
                            AboutFeatureRow(
                                icon = Icons.Default.Screenshot,
                                iconColor = Coral200,
                                iconBg = Coral200.copy(0.15f),
                                title = stringResource(R.string.screenshots),
                                description = stringResource(R.string.screenshots_desc)
                            )
                        }

                        Spacer(Modifier.height(28.dp))

                        // ── Connect section ───────────────────────────────────
                        AboutSectionLabel(
                            text = stringResource(R.string.connect_with_us),
                            color = Purple200
                        )

                        Spacer(Modifier.height(16.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            AboutSocialButton(
                                icon = Icons.Default.Email,
                                label = stringResource(R.string.email),
                                color = Color(0xFFEA4335),
                                onClick = {
                                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = "mailto:support@vidoplay.com".toUri()
                                        putExtra(Intent.EXTRA_SUBJECT, "VidoPlay Support")
                                    }
                                    context.startActivity(intent)
                                }
                            )
                            AboutSocialButton(
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
                            AboutSocialButton(
                                icon = Icons.Default.Share,
                                label = stringResource(R.string.share),
                                color = Color(0xFF25D366),
                                onClick = {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "Check out VidoPlay — https://play.google.com/store/apps/details?id=${context.packageName}"
                                        )
                                        type = "text/plain"
                                    }
                                    context.startActivity(
                                        Intent.createChooser(shareIntent, "Share VidoPlay")
                                    )
                                }
                            )
                        }

                        Spacer(Modifier.height(32.dp))

                        // ── Divider ───────────────────────────────────────────
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = if (isDark) Color.White.copy(0.07f) else Color.Black.copy(0.07f)
                        )

                        Spacer(Modifier.height(20.dp))

                        // ── Footer ────────────────────────────────────────────
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        ) {
                            Text("© 2024 VidoPlay", fontSize = 12.sp, color = textTertiary)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                stringResource(R.string.made_with_love),
                                fontSize = 12.sp,
                                color = textTertiary
                            )
                            Spacer(Modifier.height(12.dp))
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                TextButton(onClick = {}) {
                                    Text(
                                        stringResource(R.string.privacy_policy),
                                        fontSize = 12.sp,
                                        color = textSecondary
                                    )
                                }
                                Text("•", color = textTertiary, fontSize = 12.sp)
                                TextButton(onClick = {}) {
                                    Text(
                                        stringResource(R.string.terms_of_service),
                                        fontSize = 12.sp,
                                        color = textSecondary
                                    )
                                }
                                Text("•", color = textTertiary, fontSize = 12.sp)
                                TextButton(onClick = {}) {
                                    Text(
                                        stringResource(R.string.open_source_licenses),
                                        fontSize = 12.sp,
                                        color = textSecondary
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

@Composable
private fun AboutGlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val isDark = GlassTokens.isDarkTheme()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isDark) Color.White.copy(0.05f) else Color.White.copy(0.65f)
            )
            .border(
                1.dp,
                if (isDark) Color.White.copy(0.10f) else Color.Black.copy(0.06f),
                RoundedCornerShape(20.dp)
            )
    ) { content() }
}

@Composable
private fun AboutStatCard(
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val isDark = GlassTokens.isDarkTheme()
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isDark) Color.White.copy(0.045f) else Color.White.copy(0.70f)
            )
            .border(
                1.dp,
                if (isDark) Color.White.copy(0.09f) else Color.Black.copy(0.06f),
                RoundedCornerShape(16.dp)
            )
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = color)
            Spacer(Modifier.height(4.dp))
            Text(label, fontSize = 11.sp, color = GlassTokens.getTextSecondary())
        }
    }
}

@Composable
private fun AboutSectionLabel(text: String, color: Color) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = color,
        letterSpacing = 1.2.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    )
}

@Composable
private fun AboutFeatureRow(
    icon: ImageVector,
    iconColor: Color,
    iconBg: Color,
    title: String,
    description: String
) {
    val isDark = GlassTokens.isDarkTheme()
    val textPrimary   = GlassTokens.getTextPrimary()
    val textSecondary = GlassTokens.getTextSecondary()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isDark) Color.White.copy(0.045f) else Color.White.copy(0.70f)
            )
            .border(
                1.dp,
                if (isDark) Color.White.copy(0.09f) else Color.Black.copy(0.06f),
                RoundedCornerShape(16.dp)
            )
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(iconBg)
                .border(1.dp, iconColor.copy(0.25f), RoundedCornerShape(13.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = textPrimary)
            Spacer(Modifier.height(3.dp))
            Text(description, fontSize = 12.sp, color = textSecondary, lineHeight = 17.sp)
        }
    }
}

@Composable
private fun AboutSocialButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    val textSecondary = GlassTokens.getTextSecondary()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(color.copy(0.15f))
                .border(1.dp, color.copy(0.25f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(26.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(label, fontSize = 12.sp, color = textSecondary, fontWeight = FontWeight.Medium)
    }
}
