package com.foss.vidoplay.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun Modifier.glass(
    cornerRadius: Dp = 20.dp,
    bgAlpha: Float = 0.55f,
    borderTopAlpha: Float = 0.28f,
    borderBottomAlpha: Float = 0.06f
): Modifier {
    val isDark = GlassTokens.isDarkTheme()
    val bgColor1 = if (isDark) Color(0xFF1A2235) else Color(0xFFE8EAEF)
    val bgColor2 = if (isDark) Color(0xFF0D1117) else Color(0xFFF5F5F5)
    val borderColorStart = if (isDark) Color.White else Color.Black
    val borderColorEnd = if (isDark) Color.White else Color.Black

    return this
        .clip(RoundedCornerShape(cornerRadius))
        .background(
            Brush.linearGradient(
                0.0f to bgColor1.copy(alpha = bgAlpha),
                1.0f to bgColor2.copy(alpha = bgAlpha * 0.85f)
            )
        )
        .border(
            width = 1.dp,
            brush = Brush.linearGradient(
                0.0f to borderColorStart.copy(alpha = borderTopAlpha),
                0.6f to borderColorStart.copy(alpha = borderTopAlpha * 0.5f),
                1.0f to borderColorEnd.copy(alpha = borderBottomAlpha)
            ),
            shape = RoundedCornerShape(cornerRadius)
        )
}

@Composable
fun Modifier.glassPill(cornerRadius: Dp = 50.dp): Modifier {
    val chipBg = GlassTokens.getChipBg()
    val chipBorder = GlassTokens.getChipBorder()
    return this
        .clip(RoundedCornerShape(cornerRadius))
        .background(chipBg)
        .border(1.dp, chipBorder, RoundedCornerShape(cornerRadius))
}


@Composable
fun Modifier.glassPanel(cornerRadius: Dp = 24.dp): Modifier {
    val isDark = GlassTokens.isDarkTheme()
    val bgColor1 = if (isDark) Color(0xFF101828) else Color(0xFFF0F2F5)
    val bgColor2 = if (isDark) Color(0xFF0A0F1A) else Color(0xFFFAFAFA)
    val borderColorStart = if (isDark) Color.White else Color.Black
    val borderColorMid = if (isDark) Color.White else Color.Black
    val borderColorEnd = if (isDark) Color.White else Color.Black

    return this
        .clip(RoundedCornerShape(cornerRadius))
        .background(
            Brush.linearGradient(
                0.0f to bgColor1.copy(0.80f),
                1.0f to bgColor2.copy(0.90f)
            )
        )
        .border(
            width = 1.dp,
            brush = Brush.linearGradient(
                0.0f to borderColorStart.copy(0.20f),
                0.4f to borderColorMid.copy(0.08f),
                1.0f to borderColorEnd.copy(0.03f)
            ),
            shape = RoundedCornerShape(cornerRadius)
        )
}


@Composable
fun Modifier.glassCard(cornerRadius: Dp = 16.dp): Modifier {
    val isDark = GlassTokens.isDarkTheme()
    val bgColor1 = if (isDark) Color(0xFF1E2024) else Color(0xFFF8F9FA)
    val bgColor2 = if (isDark) Color(0xFF141518) else Color(0xFFFFFFFF)
    val borderColorStart = if (isDark) Color.White else Color.Black
    val borderColorMid = if (isDark) Color.White else Color.Black
    val borderColorEnd = if (isDark) Color.White else Color.Black

    return this
        .clip(RoundedCornerShape(cornerRadius))
        .background(
            Brush.linearGradient(
                0.0f to bgColor1.copy(alpha = 0.85f),
                1.0f to bgColor2.copy(alpha = 0.90f)
            )
        )
        .border(
            width = 0.8.dp,
            brush = Brush.linearGradient(
                0.0f to borderColorStart.copy(alpha = 0.12f),
                0.5f to borderColorMid.copy(alpha = 0.06f),
                1.0f to borderColorEnd.copy(alpha = 0.03f)
            ),
            shape = RoundedCornerShape(cornerRadius)
        )
}


@Composable
fun Modifier.glassChip(cornerRadius: Dp = 8.dp): Modifier {
    val isDark = GlassTokens.isDarkTheme()
    val bgColor = if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f)
    val borderColor = if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.10f)

    return this
        .clip(RoundedCornerShape(cornerRadius))
        .background(bgColor)
        .border(0.5.dp, borderColor, RoundedCornerShape(cornerRadius))
}

object GlassTokens {

    @Composable
    @ReadOnlyComposable
    fun isDarkTheme(): Boolean {
        return MaterialTheme.colorScheme.background.luminance() < 0.5
    }

    @Composable
    @ReadOnlyComposable
    fun getChipBg(): Color {
        return if (isDarkTheme()) Color.White.copy(alpha = 0.10f)
        else Color.Black.copy(alpha = 0.08f)
    }

    @Composable
    @ReadOnlyComposable
    fun getChipBorder(): Color {
        return if (isDarkTheme()) Color.White.copy(alpha = 0.18f)
        else Color.Black.copy(alpha = 0.15f)
    }

    @Composable
    @ReadOnlyComposable
    fun getTopScrim(): Brush {
        return Brush.verticalGradient(
            listOf(
                Color.Black.copy(alpha = 0.65f),
                Color.Black.copy(alpha = 0.25f),
                Color.Transparent
            )
        )
    }

    @Composable
    @ReadOnlyComposable
    fun getBottomScrim(): Brush {
        return Brush.verticalGradient(
            listOf(
                Color.Transparent,
                Color.Black.copy(alpha = 0.35f),
                Color.Black.copy(alpha = 0.75f)
            )
        )
    }

    val RedAccent = Color(0xFFE50914)
    val AmberDot = Color(0xFFFDD835)

    @Composable
    @ReadOnlyComposable
    fun getTextPrimary(): Color {
        return if (isDarkTheme()) Color.White else Color(0xFF1A1A1A)
    }

    @Composable
    @ReadOnlyComposable
    fun getTextSecondary(): Color {
        return if (isDarkTheme()) Color.White.copy(0.65f) else Color.Black.copy(0.65f)
    }

    @Composable
    @ReadOnlyComposable
    fun getTextTertiary(): Color {
        return if (isDarkTheme()) Color.White.copy(0.40f) else Color.Black.copy(0.40f)
    }
}